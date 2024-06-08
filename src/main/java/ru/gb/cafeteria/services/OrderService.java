package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.Order;
import ru.gb.cafeteria.domain.OrderStatus;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.repository.OrderRepository;
import ru.gb.cafeteria.repository.OrderStatusRepository;
import ru.gb.cafeteria.repository.ReceiptRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private OrderStatusRepository orderStatusRepo;
    @Autowired
    private ReceiptRepository receiptRepo;


    public OrderStatus getOrderStatusByName(String name) {
        return orderStatusRepo.findByName(name);
    }

    public List<Order> getOrdersByStatus(String status) {
        OrderStatus orderStatus = orderStatusRepo.findByName(status);
        return orderRepo.findByOrderStatus(orderStatus);
    }

    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        OrderStatus orderStatus = orderStatusRepo.findByName(status);
        order.setOrderStatus(orderStatus);
        orderRepo.save(order);
    }

    public boolean areAllOrdersReady(Long receiptId) {
        Receipt currentReceipt = receiptRepo.findById(receiptId).orElseThrow();
        List<Order> orders = orderRepo.findByReceipt(currentReceipt);
        return orders.stream().allMatch(order -> order.getOrderStatus().getStatus_name().equals("READY"));
    }

    public void saveOrder(Order order) {
        orderRepo.save(order);
    }


}
