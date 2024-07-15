package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.DiscountType;
import ru.gb.cafeteria.repository.BonusCardRepository;
import ru.gb.cafeteria.repository.DiscountRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepo;
    private final BonusCardRepository cardRepo;

    public List<DiscountType> getAllDiscounts() {
        return discountRepo.findAll();
    }

    public DiscountType getDiscountById(Long id) {
        return discountRepo.findById(id).orElseThrow(() -> new RuntimeException("Discount not found"));
    }


    // шаблон настроен не пропускать некорректные значения.
    // но для пущей верности перепроверяем значения перед сохранением,
    // если некорректно - по-тихому меняем и сохраняем.
    public void saveDiscount(DiscountType discount) {
        if (discount.getDiscountPercent() < 0 || discount.getDiscountPercent() > 100) {
            discount.setDiscountPercent(0);
        }
        if (discount.getMaxSum().compareTo(discount.getMinSum()) > 0) {
            discountRepo.save(discount);
        } else {
            discount.setMaxSum(discount.getMinSum().add(BigDecimal.ONE));
            discountRepo.save(discount);
        }
    }


    @TrackUserAction
    public void deleteDiscountById(Long id) {
        if (isDeleteByIdPossible(id)) {
            discountRepo.deleteById(id);
        }
    }


    // проверяем - возможно ли удаление дисконта
    // удалить можно только те дисконты, которые не привязаны к карточкам
    public Boolean isDeleteByIdPossible(Long id) {
        long count = cardRepo.countByDiscountType_DiscountId(id);
        return count == 0;
    }

    public void changeActivityById(Long id, boolean active) {
        DiscountType discount = getDiscountById(id);
        discount.setActive(active);
        saveDiscount(discount);
    }
}
