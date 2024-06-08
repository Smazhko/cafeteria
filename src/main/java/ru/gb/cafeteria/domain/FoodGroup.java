package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "food_groups")
@Data
public class FoodGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, unique = true)
    private String name;

//    @OneToMany(mappedBy = "foodGroup")
//    private List<MenuItem> menuItems;
}
