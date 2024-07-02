package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "receipt_statuses")
@Data
public class ReceiptStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_status_id")
    private Long receiptStatusId;

    @Column(name = "status_name", nullable = false, unique = true)
    private String statusName;
}
