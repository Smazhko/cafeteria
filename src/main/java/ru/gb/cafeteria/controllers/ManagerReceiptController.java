package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.domain.ReceiptStatus;
import ru.gb.cafeteria.services.ReceiptService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/manager/receipts")
public class ManagerReceiptController {
    private final ReceiptService receiptService;

    @GetMapping
    public String getAllReceipts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long statusId,
            Model model) {

        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : LocalDate.now();
        List<Receipt> receipts = receiptService.getFilteredReceipts(start, end, statusId);
        List<ReceiptStatus> statuses = receiptService.getAllReceiptStatuses();

        // Вычисление итоговых значений
        BigDecimal totalSum = receipts.stream()
                .map(Receipt::getTotalSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountSum = receipts.stream()
                .map(Receipt::getDiscountSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalSum = receipts.stream()
                .map(Receipt::getFinalSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("receipts", receipts);
        model.addAttribute("statuses", statuses);
        model.addAttribute("selectedStatus", statusId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("totalSum", totalSum);
        model.addAttribute("discountSum", discountSum);
        model.addAttribute("finalSum", finalSum);

        return "manager/receipts";
    }

    @PostMapping("/delete")
    public String deleteReceipt(@RequestParam("id") Long id) {
        receiptService.deleteReceiptById(id);
        return "redirect:/manager/receipts";
    }
}
