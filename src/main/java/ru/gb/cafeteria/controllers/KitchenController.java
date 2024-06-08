package ru.gb.cafeteria.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.Order;
import ru.gb.cafeteria.services.OrderService;
import ru.gb.cafeteria.services.ReceiptService;

import java.util.List;

@RestController
@RequestMapping("/kitchen")
public class KitchenController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReceiptService receiptService;

    @GetMapping("/orders")
    public List<Order> getKitchenOrders() {
        return orderService.getOrdersByStatus("SEND TO KITCHEN");
    }

    @PostMapping("/order/ready")
    public void markOrderAsReady(@RequestParam Long orderId) {
        Order order = orderService.getOrderById(orderId);
        orderService.updateOrderStatus(orderId, "READY");

        Long receiptId = order.getReceipt().getReceiptId();
        if (orderService.areAllOrdersReady(receiptId)) {
            receiptService.closeReceipt(receiptId);
        }
    }
}