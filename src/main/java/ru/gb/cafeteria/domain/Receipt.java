package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "close_time", nullable = false)
    private LocalDateTime closeTime;

    @ManyToOne
    @JoinColumn(name = "receipt_status_id", nullable = false)
    private ReceiptStatus receiptStatus;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private BonusCard bonusCard;

    @Column(name = "total_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSum;

    @OneToMany(mappedBy = "receipt")
    private List<Order> orderList;
}
