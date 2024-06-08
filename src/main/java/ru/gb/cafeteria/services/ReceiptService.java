package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.domain.ReceiptStatus;
import ru.gb.cafeteria.repository.OrderRepository;
import ru.gb.cafeteria.repository.ReceiptRepository;
import ru.gb.cafeteria.repository.ReceiptStatusRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepo;
    @Autowired
    private ReceiptStatusRepository rStatusRepo;
    @Autowired
    private OrderRepository orderRepo;

    public List<Receipt> getAllReceipts() {
        return receiptRepo.findAll();
    }

    public List<Receipt> getAllClosedReceipts() {
        ReceiptStatus closedStatus = rStatusRepo.findByName("CLOSED");
        return receiptRepo.findByReceiptStatus(closedStatus);
    }

    public Receipt getReceiptById(Long id) {
        return receiptRepo.findById(id).orElseThrow(() -> new RuntimeException("Receipt not found"));
    }

    public ReceiptStatus getReceiptStatusByName(String name) {
        return rStatusRepo.findByName(name);
    }

    public void saveReceipt(Receipt receipt) {
        receiptRepo.save(receipt);
    }

    public void closeReceipt(Long receiptId) {
        Receipt receipt = getReceiptById(receiptId);
        ReceiptStatus closedStatus = getReceiptStatusByName("CLOSED");
        receipt.setReceiptStatus(closedStatus);
        receiptRepo.save(receipt);
    }

}
