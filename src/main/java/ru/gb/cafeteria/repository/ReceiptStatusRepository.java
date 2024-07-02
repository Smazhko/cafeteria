package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.ReceiptStatus;

@Repository
public interface ReceiptStatusRepository extends JpaRepository<ReceiptStatus, Long> {
    ReceiptStatus findByStatusName(String statusName);
}
