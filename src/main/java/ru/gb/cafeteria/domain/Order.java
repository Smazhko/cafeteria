package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal sum;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Staff chef;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("orderId", orderId)
                .append("menuItem", menuItem.getName())
                .append("price", price)
                .append("quantity", quantity)
                .append("sum", sum)
                .append("orderStatus", orderStatus)
                .append("receipt", receipt.getReceiptId())
                .append("chef", chef.getName())
                .toString();
    }
}

