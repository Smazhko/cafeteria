package ru.gb.cafeteria.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.repository.DiscountRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepo;

    public List<DiscountType> getAllDiscounts() {
        return discountRepo.findAll();
    }

    public DiscountType getDiscountById(Long id) {
        return discountRepo.findById(id).orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public void saveDiscount(DiscountType discount) {
        discountRepo.save(discount);
    }

    public void deleteDiscountById(Long id) {
        discountRepo.deleteById(id);
    }
}
