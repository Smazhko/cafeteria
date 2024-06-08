package ru.gb.cafeteria.domain;


import lombok.Data;

@Data
public class OrderItem {
    private Long menuItemId;
    private int quantity;
}
