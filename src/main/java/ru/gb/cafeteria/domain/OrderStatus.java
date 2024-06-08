package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_statuses")
@Data
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    private Long orderStatusId;

    @Column(name = "status_name", nullable = false, unique = true)
    private String status_name;
}