package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT r.clientCode FROM Receipt r WHERE r.openTime >= :startOfDay AND r.openTime < :endOfDay ORDER BY r.clientCode DESC")
    List<String> findClientCodesForToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    List<Receipt> findByReceiptStatusAndReceivedFalse(ReceiptStatus closedStatus);
}

