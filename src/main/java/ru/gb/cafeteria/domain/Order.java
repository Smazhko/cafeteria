package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long order_id;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff chef;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;
}
