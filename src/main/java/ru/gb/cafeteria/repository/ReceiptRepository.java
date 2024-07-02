package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.domain.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByReceiptStatus(ReceiptStatus receiptStatus);
    List<Receipt> findAllByOpenTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Receipt> findAllByOpenTimeBetweenAndReceiptStatus(LocalDateTime start, LocalDateTime end, ReceiptStatus status);
    List<Receipt> findAllByOpenTimeBefore(LocalDateTime end);
    List<Receipt> findAllByOpenTimeBeforeAndReceiptStatus(LocalDateTime end, ReceiptStatus status);
}
