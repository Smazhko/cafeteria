package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "food_groups")
@Data
public class FoodGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, unique = true)
    private String name;
}
