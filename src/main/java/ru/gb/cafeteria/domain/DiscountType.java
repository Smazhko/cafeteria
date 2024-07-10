package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
public class DiscountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "min_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal minSum = BigDecimal.ZERO;

    @Column(name = "max_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxSum = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    private Boolean active;

}
    // @OneToMany(mappedBy = "discount")
    // private Set<BonusCard> bonusCards;
