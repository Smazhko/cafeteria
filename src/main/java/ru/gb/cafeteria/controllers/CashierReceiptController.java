package ru.gb.cafeteria.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;
import ru.gb.cafeteria.bonusSystem.services.BonusCardService;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.exceptions.UnauthorizedAccessException;
import ru.gb.cafeteria.services.BasketService;
import ru.gb.cafeteria.services.MenuService;
import ru.gb.cafeteria.services.OrderService;
import ru.gb.cafeteria.services.ReceiptService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/cashier/receipt")
public class CashierReceiptController {

    private MenuService menuService;
    private ReceiptService receiptService;
    private OrderService orderService;
    private BonusCardService bonusCardService;
    private BasketService basketService;


    @TrackUserAction
    @GetMapping
    public String showReceipt(@RequestParam(required = false) Long cardId, HttpSession session, Model model) {

        BasketDTO basket = basketService.getBasket(session);
        Receipt receipt = receiptService.createNewReceipt(basket, session);

        model.addAttribute("receipt", receipt);
        model.addAttribute("searchIsEmpty", true);
        if (cardId != null) {
            BonusCard bonusCard = bonusCardService.getBonusCardById(cardId);
            model.addAttribute("bonusCard", bonusCard);
            model.addAttribute("searchIsEmpty", false);
        }
        return "cashier/receipt";
    }


    @TrackUserAction
    @PostMapping("/pay/{receiptId}")
    public String payReceipt(@PathVariable Long receiptId, @RequestParam(required = false) Long cardId, HttpSession session) {
        System.out.println("PAYMENT PROCEED... " + receiptId);
        receiptService.payReceiptById(receiptId, session);
        if (cardId != null) {
            bonusCardService.refreshDiscount(cardId);
        }
        basketService.clearBasket(session);
        return "redirect:/cashier/menu";
    }


    @TrackUserAction
    @PostMapping("/edit/{receiptId}")
    public String editReceipt(@PathVariable Long receiptId, Model model, HttpSession session) {
        try {
            receiptService.cancelReceipt(receiptId, session);
            return "redirect:/cashier/menu"; // Перенаправление на страницу меню
        } catch (UnauthorizedAccessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error-exception"; // Имя HTML страницы для отображения ошибки
        }
    }


    @TrackUserAction
    @PostMapping("/cancel/{receiptId}")
    public String receiptCancel(@PathVariable Long receiptId, Model model, HttpSession session) {
        try {
            receiptService.cancelReceipt(receiptId, session);
            basketService.clearBasket(session);
            return "redirect:/cashier/menu"; // Перенаправление на страницу меню
        } catch (UnauthorizedAccessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error-exception"; // Имя HTML страницы для отображения ошибки
        }
    }


    @TrackUserAction
    @GetMapping("/search")
    @ResponseBody
    public List<BonusCard> searchBonusCard(@RequestParam("search") String search) {
        return bonusCardService.searchByPhone(search);
    }

    @TrackUserAction
    @GetMapping("/reset-search")
    public String resetSearch(Model model) {
        model.addAttribute("bonusSearch", "");
        model.addAttribute("searchIsEmpty", false);
        return "redirect:/cashier/receipt";
    }

    @TrackUserAction
    @PostMapping("/apply-discount")
    public String applyDiscount(@RequestParam("cardId") Long cardId, @RequestParam("receiptId") Long receiptId, Model model, HttpSession session) {
        Receipt receipt = receiptService.applyBonusCard(receiptId, cardId);
        BonusCard appliedCard = bonusCardService.getBonusCardById(cardId);
        receiptService.setCurrentReceipt(receipt, session);
        model.addAttribute("receipt", receipt);
        model.addAttribute("bonusCard", appliedCard);
        model.addAttribute("searchIsEmpty", false);
        return "cashier/receipt";
    }


    @TrackUserAction
    @PostMapping("/create-card")
    public String createCard(@ModelAttribute BonusCard newCard, @RequestParam Long receiptId, Model model) {
        BonusCard createdCard = bonusCardService.createCard(newCard);
        Receipt receipt = receiptService.getReceiptById(receiptId);
        receiptService.applyBonusCard(receiptId, createdCard.getCardId());
        model.addAttribute("receipt", receipt);
        model.addAttribute("bonusCard", createdCard);
        model.addAttribute("searchIsEmpty", false);
        return "cashier/receipt";
    }

}

