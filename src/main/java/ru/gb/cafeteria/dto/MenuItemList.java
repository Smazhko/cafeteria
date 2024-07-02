package ru.gb.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gb.cafeteria.domain.MenuItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemList {
    private List<MenuItem> itemList;
}
