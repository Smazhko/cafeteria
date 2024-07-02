package ru.gb.cafeteria.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.dto.MenuItemList;
import ru.gb.cafeteria.bonusSystem.services.BonusCardService;
import ru.gb.cafeteria.exceptions.UnauthorizedAccessException;
import ru.gb.cafeteria.services.MenuService;
import ru.gb.cafeteria.services.OrderService;
import ru.gb.cafeteria.services.ReceiptService;

import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BonusCardService bonusCardService;

    // воспользуемся объектом-прослойкой (DTO) для передачи данных в HTML форму для формирования заказа
    @GetMapping("/menu")
    public String getMenuPage(Model model) {
        MenuItemList menuItemList = new MenuItemList(menuService.getActiveMenuItems());
        model.addAttribute("menuItemList", menuItemList);
        return "menu";
    }

    // попробовать земенить MenuItemList menuItemList на List<MenuItem>
    @PostMapping("/receipt-forming")
    public String receiptForming(@ModelAttribute MenuItemList menuItemList, Model model) {
        // фильтруем список и оставляем только те блюда, количество которых больше 0
        List<MenuItem> filteredItems = menuItemList.getItemList().stream()
                .filter(item -> item.getQuantity() > 0)
                .toList();

        // СФОРМИРОВАТЬ ЧЕК:
        // 1. создать новый пустой чек
        // 2. сформировать список заказов на основании отфильтрованных пунктов меню
        // 3. сохранить список заказов в чек
        // 4. сохранить чек
        Receipt receipt = receiptService.createNewReceipt();
        ReceiptStatus readyToPayStatus = receiptService.getReceiptStatusById(2L);

        List<Order> orderList = orderService.createOrderList(receipt, filteredItems);

        receipt.setOrderList(orderList);
        receiptService.recalculateReceipt(receipt);
        receipt.setReceiptStatus(readyToPayStatus);
        receiptService.saveReceipt(receipt);

        System.out.println(receipt.getReceiptId() + ", "
                + receipt.getTotalSum() + ", "
                + receipt.getReceiptStatus().getStatusName());

        model.addAttribute("receipt", receipt);


        /*

        // Получаем бонусную карту
        BonusCard bonusCard = bonusService.getBonusCardById(cardId);

        // Используем бонусы
        Receipt receipt = // ... создаем и сохраняем чек
        if (usedBonus.compareTo(BigDecimal.ZERO) > 0) {
            bonusService.useBonus(bonusCard, receipt, usedBonus);
        }

        model.addAttribute("receipt", receipt);
        return "order_confirmation"; // Страница подтверждения заказа
         */

        return "receipt";
    }

    @PostMapping("/receipt-submit/{receiptId}")
    public String receiptSubmit(@PathVariable Long receiptId) {
        System.out.println("SUBMIT... " + receiptId);
        Receipt receipt = receiptService.getReceiptById(receiptId);
        orderService.updateOrdersStatuses(
                receipt.getOrderList(),
                orderService.getOrderStatusByName("IN PROGRESS"));
        receiptService.closeReceipt(receipt);

        return "redirect:/menu";
    }

    @PostMapping("/receipt-cancel/{receiptId}")
    public String receiptCancel(@PathVariable Long receiptId, Model model) {
        try {
            receiptService.cancelReceipt(receiptId);
            return "redirect:/menu"; // Перенаправление на страницу меню
        } catch (UnauthorizedAccessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error-exception"; // Имя HTML страницы для отображения ошибки
        }
    }
}

