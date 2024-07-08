package ru.gb.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketDTO {
    private List<BasketItemDTO> items = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public BigDecimal getTotalCost() {
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getMenuItem().getSpecialPrice() != null && item.getMenuItem().getSpecialPrice().compareTo(item.getMenuItem().getPrice()) < 0
                            ? item.getMenuItem().getSpecialPrice()
                            : item.getMenuItem().getPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
