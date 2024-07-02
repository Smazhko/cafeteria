package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.domain.ReceiptStatus;
import ru.gb.cafeteria.exceptions.UnauthorizedAccessException;
import ru.gb.cafeteria.repository.ReceiptRepository;
import ru.gb.cafeteria.repository.ReceiptStatusRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReceiptService {

    private ReceiptRepository receiptRepo;
    private ReceiptStatusRepository rStatusRepo;

    public ReceiptStatus getReceiptStatusById(Long id) {
        return rStatusRepo.findById(id).orElseThrow();
    }

    public ReceiptStatus getReceiptStatusByName(String name) {
        return rStatusRepo.findByStatusName(name);
    }


    public List<Receipt> getFilteredReceipts(LocalDate startDate, LocalDate endDate, Long statusId) {
        if (startDate == null) {
            return (statusId == null || statusId == 0) ?
                    receiptRepo.findAllByOpenTimeBefore(endDate.atStartOfDay().plusDays(1)) :
                    receiptRepo.findAllByOpenTimeBeforeAndReceiptStatus(endDate.atStartOfDay().plusDays(1), rStatusRepo.findById(statusId).orElseThrow());
        } else {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atStartOfDay().plusDays(1);
            return (statusId == null || statusId == 0) ?
                    receiptRepo.findAllByOpenTimeBetween(start, end) :
                    receiptRepo.findAllByOpenTimeBetweenAndReceiptStatus(start, end, rStatusRepo.findById(statusId).orElseThrow());
        }
    }

    public List<Receipt> getAllReceipts() {
        return receiptRepo.findAll();
    }

    public List<Receipt> getAllClosedReceipts() {
        ReceiptStatus closedStatus = rStatusRepo.findByStatusName("CLOSED");
        return receiptRepo.findByReceiptStatus(closedStatus);
    }

    public Receipt getReceiptById(Long receiptId) {
        return receiptRepo.findById(receiptId).orElseThrow(() -> new RuntimeException("Receipt not found"));
    }

    public Receipt createNewReceipt() {
        ReceiptStatus openStatus = getReceiptStatusById(1L);

        Receipt newReceipt = new Receipt();
        newReceipt.setOpenTime(LocalDateTime.now());
        newReceipt.setReceiptStatus(openStatus);
        saveReceipt(newReceipt);
        return newReceipt;
    }

    public void saveReceipt(Receipt receipt) {
        if (receipt.getDiscountSum() == null)
            receipt.setDiscountSum(BigDecimal.ZERO);
        receipt.setFinalSum(receipt.getTotalSum().subtract(receipt.getDiscountSum()));
        receiptRepo.save(receipt);
    }

    public void closeReceipt(Receipt receipt) {
        ReceiptStatus closedStatus = getReceiptStatusById(4L);
        receipt.setReceiptStatus(closedStatus);
        receiptRepo.save(receipt);
    }

    // отмена чека по ID
    // если чек имеет статус FORMING или READY TO PAY, то он полностью удаляется.
    // оплаченные чеки удалить нельзя!
    public void cancelReceipt(Long receiptId) throws UnauthorizedAccessException {
        Receipt receipt = getReceiptById(receiptId);
        ReceiptStatus formingStatus = getReceiptStatusById(1L);
        ReceiptStatus readyToPayStatus = getReceiptStatusById(2L);

        System.out.println("DELETING ... " + receipt.getReceiptStatus());

        if (receipt.getReceiptStatus().equals(formingStatus) ||
                receipt.getReceiptStatus().equals(readyToPayStatus) ){
            receiptRepo.deleteById(receiptId); // каскадное удаление заказов вслед за удалением чека
        }

        else
            throw new UnauthorizedAccessException("Удаление чека со статусом \"PAID\" запрещено. ");

        System.out.println("DELETING ... " + receipt.getReceiptId());
    }


    // удаление чеков для менеджера
    public void deleteReceiptById(Long id) {
        receiptRepo.deleteById(id);
    }


    public void recalculateReceipt(Receipt receipt) {
        BigDecimal totalSum = BigDecimal.ZERO;

        totalSum = receipt.getOrderList().stream()
                .map(order -> order.getMenuItem().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (receipt.getBonusCard() != null) {
            BonusCard card = receipt.getBonusCard();
            Integer discountPercent = card.getDiscount().getDiscountPercent();
            Integer bonusPercent = card.getDiscount().getBonusPercent();
            totalSum = totalSum.multiply(BigDecimal.valueOf(100 - discountPercent));
        }
        receipt.setTotalSum(totalSum);

        // снятие бонусов, начисление бонусов.

    }

    public List<ReceiptStatus> getAllReceiptStatuses() {
        return rStatusRepo.findAll();
    }
}
