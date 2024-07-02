package ru.gb.cafeteria.bonusSystem.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bonus_cards")
@Data
public class BonusCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_phone", nullable = false, unique = true)
    private String clientPhone;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "time_register", nullable = false)
    private LocalDateTime timeRegister;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Column(name = "total_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSum = BigDecimal.ZERO;

    @Column(name = "bonus_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal bonusSum = BigDecimal.ZERO;

    @OneToMany(mappedBy = "bonusCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonusTransaction> transactions;

}
