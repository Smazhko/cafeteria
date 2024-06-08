package ru.gb.cafeteria.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.services.BonusCardService;
import ru.gb.cafeteria.services.MenuService;
import ru.gb.cafeteria.services.OrderService;
import ru.gb.cafeteria.services.ReceiptService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @GetMapping("/menu")
    public String getMenuPage(Model model) {
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        return "menu";
    }


    @PostMapping("/order")
    public String placeOrder(@ModelAttribute("items") List<OrderItem> items, @RequestParam Long cardId, Model model) {
        BonusCard bonusCard = bonusCardService.getBonusCardById(cardId);
        ReceiptStatus openStatus = receiptService.getReceiptStatusByName("OPEN");
        Receipt receipt = new Receipt();
        receipt.setOpenTime(LocalDateTime.now());
        receipt.setReceiptStatus(openStatus);
        receipt.setBonusCard(bonusCard);

        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderItem orderItem : items) {
            MenuItem menuItem = menuService.getMenuItemById(orderItem.getMenuItemId());
            int quantity = orderItem.getQuantity();
            if (quantity > 0) {
                BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
                totalSum = totalSum.add(itemTotal);

                // формируем заказ блюда для кухни:
                // устанавливаем связь с чеком, блюдо, количество, статус "СФОРМИРОВАН" и сохраняем в базу.
                Order order = new Order();
                order.setReceipt(receipt);
                order.setMenuItem(menuItem);
                order.setCount(quantity);
                order.setOrderStatus(orderService.getOrderStatusByName("FORMED"));
                orderService.saveOrder(order);
            }
        }

        receipt.setTotalSum(totalSum);
        receiptService.saveReceipt(receipt);

        model.addAttribute("receipt", receipt);
        return "order_confirmation"; // Страница подтверждения заказа
    }

    @PostMapping("/payReceipt")
    public String payReceipt(@RequestParam Long receiptId, Model model) {
        Receipt receipt = receiptService.getReceiptById(receiptId);
        ReceiptStatus paidStatus = receiptService.getReceiptStatusByName("PAID");
        receipt.setReceiptStatus(paidStatus);
        //receipt.setCloseTime(LocalDateTime.now());
        receiptService.saveReceipt(receipt);

//        List<Order> orders = receipt.getOrderList();
//        OrderStatus formedStatus = orderService.getOrderStatusByName("FORMED");
//        for (Order order : orders) {
//            order.setOrderStatus(formedStatus);
//            orderService.saveOrder(order);
//        }

        model.addAttribute("receipt", receipt);
        return "order_confirmation"; // Обновить страницу подтверждения заказа
    }

    @PostMapping("/sendToKitchen")
    public String sendToKitchen(@RequestParam Long receiptId, Model model) {
        Receipt receipt = receiptService.getReceiptById(receiptId);

        List<Order> orders = receipt.getOrderList();
        OrderStatus kitchenStatus = orderService.getOrderStatusByName("RECEIVED BY KITCHEN");
        for (Order order : orders) {
            order.setOrderStatus(kitchenStatus);
            orderService.saveOrder(order);
        }

        model.addAttribute("receipt", receipt);
        // если заказ уже оплачен, то возвращаемся к меню
        // если не оплачен, то обновляем страницу подтверждения заказа
        if (receipt.getReceiptStatus().equals(receiptService.getReceiptStatusByName("PAID"))) {
            return "menu";
        }
        else {
            return "order_confirmation"; // Обновить страницу подтверждения заказа
        }
    }

}