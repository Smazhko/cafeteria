package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private FoodGroup foodGroup;

    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "special_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal specialPrice;

    @Column(name = "image")
    private String imageURL;

    @Column(name="is_active", nullable = false)
    private Boolean active;

    @Column(name="is_archived", nullable = false)
    private Boolean archived;

    @Column(name="status_change_time", nullable = false)
    private LocalDateTime statusChangeTime;
}
