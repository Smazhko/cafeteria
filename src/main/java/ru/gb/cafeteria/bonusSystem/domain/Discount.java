package ru.gb.cafeteria.bonusSystem.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "discounts")
@Data
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "bonus_percent")
    private Integer bonusPercent;

    @Column(name = "description")
    private String description;

    @Column(name = "date_begin")
    private LocalDateTime dateBegin;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "discount")
    private Set<BonusCard> bonusCards;
}
