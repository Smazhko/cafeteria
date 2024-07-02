package ru.gb.cafeteria.bonusSystem.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;
import ru.gb.cafeteria.bonusSystem.domain.BonusTransaction;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.bonusSystem.domain.TransactionType;
import ru.gb.cafeteria.exceptions.DuplicatePhoneException;
import ru.gb.cafeteria.bonusSystem.repository.BonusCardRepository;
import ru.gb.cafeteria.bonusSystem.repository.BonusTransactionRepository;
import ru.gb.cafeteria.bonusSystem.repository.DiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BonusCardService {
    private BonusCardRepository cardRepo;
    private DiscountRepository discountRepo;
    private BonusTransactionRepository bonusTransactionRepo;

    // сортируем отдаваемый список по имени клиента
    public List<BonusCard> getAllBonusCards() {
        return cardRepo.findAll().stream()
                .sorted(Comparator.comparing(BonusCard::getClientName))
                .collect(Collectors.toList());
    }

    public BonusCard getBonusCardById(Long cardId) {
        return cardRepo.findById(cardId).orElseThrow();
    }

    public List<BonusCard> getBonusCardByClientName(String clientName) {
        return cardRepo.findByClientName(clientName);
    }

    public List<BonusCard> getBonusCardByClientPhone(String clientPhone) {
        return cardRepo.findByClientPhone(clientPhone);
    }

    public boolean existsByClientPhone(String phone) {
        return cardRepo.existsByClientPhone(phone);
    }

    // если телефон клиента уже есть в базе, выбрасываем исключение
    public BonusCard createBonusCard(BonusCard newCard) {
        try {
            if (!newCard.getClientName().isEmpty()
                    && !newCard.getClientPhone().isEmpty()) {
                newCard.setTimeRegister(LocalDateTime.now());
                if (newCard.getDiscount() == null) newCard.setDiscount(discountRepo.findById(1L).orElseThrow());
            }
            return newCard;
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicatePhoneException("A bonus card with phone number " + newCard.getClientPhone() + " already exists");
        }
    }

    // начисление бонусов
    @Transactional
    public void addBonus(Receipt receipt, BonusCard bonusCard, BigDecimal amount) {
        if (receipt.getCloseTime() != null && bonusCard != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            BonusTransaction transaction = new BonusTransaction(
                    bonusCard,
                    receipt,
                    receipt.getCloseTime(),
                    TransactionType.DEPOSIT,
                    amount);
            bonusTransactionRepo.save(transaction);

            bonusCard.setBonusSum(bonusCard.getBonusSum().add(amount));
            cardRepo.save(bonusCard);
        } else {
            throw new IllegalArgumentException("Некорректные данные для пополнения счёта бонусной карты.");
        }
    }

    // списание бонусов
    @Transactional
    public void useBonus(Receipt receipt, BonusCard bonusCard, BigDecimal amount) {
        if (bonusCard.getBonusSum().compareTo(amount) >= 0
                && receipt.getCloseTime() != null
                && amount.compareTo(BigDecimal.ZERO) > 0) {
            BonusTransaction transaction = new BonusTransaction(
                    bonusCard,
                    receipt,
                    receipt.getCloseTime(),
                    TransactionType.WITHDRAW,
                    amount);
            bonusTransactionRepo.save(transaction);

            bonusCard.setBonusSum(bonusCard.getBonusSum().subtract(amount));
            cardRepo.save(bonusCard);
        } else {
            throw new IllegalArgumentException("Недостаточно бонусов на карте.");
        }
    }

}
