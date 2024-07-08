package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Order> orderList;

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("receiptId", receiptId)
                .append("openTime", openTime)
                .append("closeTime", closeTime)
                .append("receiptStatus", receiptStatus)
                .append("staff", staff)
                .append("bonusCard", bonusCard)
                .append("totalSum", totalSum)
                .append("discountSum", discountSum)
                .append("finalSum", finalSum)
                .append("orderList size", orderList.size())
                .toString();
    }
}
