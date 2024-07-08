package ru.gb.cafeteria.controllers;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.Order;
import ru.gb.cafeteria.domain.OrderStatus;
import ru.gb.cafeteria.services.OrderService;
import ru.gb.cafeteria.services.ReceiptService;

import java.util.List;

@Controller
@RequestMapping("/kitchen")
@AllArgsConstructor
public class KitchenController {

    private OrderService orderService;

    private ReceiptService receiptService;

    @GetMapping
    public String getOrders(Model model) {
        OrderStatus inProgressStatus = orderService.getOrderStatusByName("IN PROGRESS");
        List<Order> orderList = orderService.getOrdersByStatus(inProgressStatus);
        orderList.stream()
                .map(order -> order.getMenuItem().getName())
                .forEach(System.out::println);
        model.addAttribute("orderList", orderList);
        return "kitchen/orders";
    }

    @PostMapping("/order")
    public String changeOrderStatus(
            @RequestParam(name = "orderId") Long orderId,
            @RequestParam(name = "status") String status, HttpSession session) {
        OrderStatus readyStatus = orderService.getOrderStatusByName("READY");
        OrderStatus cancelledStatus = orderService.getOrderStatusByName("CANCELLED");
        if (status.equals("ready"))
            orderService.updateOrderStatus(orderId, readyStatus, session);
        else if (status.equals("cancel"))
            orderService.updateOrderStatus(orderId, cancelledStatus, session);
        return "redirect:/kitchen";
    }
}