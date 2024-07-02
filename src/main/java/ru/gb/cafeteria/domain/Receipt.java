package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
@Data
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "open_time", nullable = false)
    private LocalDateTime openTime;

    @Column(name = "close_time")
    private LocalDateTime closeTime;

    @ManyToOne
    @JoinColumn(name = "receipt_status_id", nullable = false)
    private ReceiptStatus receiptStatus;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private BonusCard bonusCard;

    @Column(name = "total_sum", precision = 10, scale = 2)
    private BigDecimal totalSum;

    @Column(name = "discount_sum", precision = 10, scale = 2)
    private BigDecimal discountSum;

    @Column(name = "final_sum", precision = 10, scale = 2)
    private BigDecimal finalSum;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList;
}
