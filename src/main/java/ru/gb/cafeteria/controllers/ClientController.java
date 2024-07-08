package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.services.ReceiptService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/client")
public class ClientController {
    private ReceiptService receiptService;

    @GetMapping
    public String showReceipts(Model model){
        List<Receipt> waitReceiptList = receiptService.getAllPaidReceipts();
        List<Receipt> readyReceiptList = receiptService.getAllClosedReceipts();
        model.addAttribute("waitReceiptList", waitReceiptList);
        model.addAttribute("readyReceiptList", readyReceiptList);
        return "/client/orders";
    }

    @PostMapping("/receive/{receiptId}")
    public String receiveReceipt(@PathVariable Long receiptId) {
        receiptService.receiveReceipt(receiptId);
        return "redirect:/client";
    }
}
