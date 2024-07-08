package ru.gb.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gb.cafeteria.domain.MenuItem;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemDTO {
    private MenuItem menuItem;
    private int quantity;
}