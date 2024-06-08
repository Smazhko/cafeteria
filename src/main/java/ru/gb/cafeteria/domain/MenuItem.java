package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private FoodGroup foodGroup;

    private String description = "";

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "special_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal specialPrice;

    @Column(name = "image")
    private String imageURL;

    @Column(nullable = false)
    private Boolean active;
}
