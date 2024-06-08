package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
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
    private Integer bonus_percent;

    @Column(name = "description")
    private String description;

    @Column(name = "date_begin")
    private LocalDateTime date_begin;

    @Column(name = "date_end")
    private LocalDateTime date_end;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "discount")
    private Set<BonusCard> bonusCards;
}
