package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.BonusCard;
import ru.gb.cafeteria.exceptions.DuplicatePhoneException;
import ru.gb.cafeteria.repository.BonusCardRepository;
import ru.gb.cafeteria.repository.DiscountRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BonusCardService {
    private BonusCardRepository cardRepo;
    private DiscountRepository discountRepo;

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
}
