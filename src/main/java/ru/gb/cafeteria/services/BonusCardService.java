package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.BonusCard;
import ru.gb.cafeteria.domain.DiscountType;
import ru.gb.cafeteria.exceptions.DuplicatePhoneException;
import ru.gb.cafeteria.repository.BonusCardRepository;
import ru.gb.cafeteria.repository.DiscountRepository;

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

    // сортируем отдаваемый список по имени клиента
    public List<BonusCard> getAllBonusCards() {
        return cardRepo.findAll().stream()
                .sorted(Comparator.comparing(BonusCard::getClientName))
                .collect(Collectors.toList());
    }


    public BonusCard getBonusCardById(Long cardId) {
        return cardRepo.findById(cardId).orElseThrow();
    }


    public List<BonusCard> getBonusCardByClientPhone(String clientPhone) {
        return cardRepo.findByClientPhone(clientPhone);
    }


    public boolean existsByClientPhone(String phone) {
        return cardRepo.existsByClientPhone(phone);
    }


    public List<BonusCard> searchByPhone(String phone) {
        return cardRepo.findByClientPhoneContaining(phone, PageRequest.of(0, 10)).getContent();
    }


    public void refreshDiscount(Long cardId) {
        BonusCard card = cardRepo.findById(cardId).orElseThrow();
        BigDecimal totalSum = card.getTotalSum();
        DiscountType applicableDiscount = discountRepo.findAll().stream()
                .filter(DiscountType::getActive)
                .filter(discount -> discount.getMinSum().compareTo(totalSum) <= 0 &&
                        discount.getMaxSum().compareTo(totalSum) > 0)
                .findFirst()
                .orElseThrow();
        card.setDiscountType(applicableDiscount);
        cardRepo.save(card);
    }

    // если телефон клиента уже есть в базе, выбрасываем исключение
    public BonusCard createCard(BonusCard newCard) {
        if (!existsByClientPhone(newCard.getClientPhone())) {
            newCard.setTimeRegister(LocalDateTime.now());
            newCard.setTotalSum(BigDecimal.ZERO);
            // установка бонусной карты, подходящей на сумму накоплений, равной нулю
            DiscountType applicableDiscount = discountRepo.findAll().stream()
                    .filter(DiscountType::getActive)
                    .filter(discount -> discount.getMinSum().compareTo(BigDecimal.ZERO) <= 0 &&
                            discount.getMaxSum().compareTo(BigDecimal.ZERO) > 0)
                    .findFirst()
                    .orElseThrow();
            newCard.setDiscountType(applicableDiscount);
            return cardRepo.save(newCard);
        } else {
            throw new DuplicatePhoneException("A bonus card with phone number " + newCard.getClientPhone() + " already exists");
        }
    }


}
