package ru.gb.cafeteria.bonusSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gb.cafeteria.domain.Receipt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bonus_transactions")
@Data
public class BonusTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private BonusCard bonusCard;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType transactionType; // 'WITHDRAW', 'DEPOSIT'

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // конструктор без ID
    public BonusTransaction(BonusCard bonusCard, Receipt receipt, LocalDateTime transactionTime, TransactionType transactionType, BigDecimal amount) {
        this.bonusCard = bonusCard;
        this.receipt = receipt;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
        this.amount = amount;
    }
}