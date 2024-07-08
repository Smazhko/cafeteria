package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;
import ru.gb.cafeteria.repository.OrderRepository;
import ru.gb.cafeteria.repository.OrderStatusRepository;
import ru.gb.cafeteria.repository.ReceiptRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepo;
    private OrderStatusRepository orderStatusRepo;
    private ReceiptRepository receiptRepo;


    public OrderStatus getOrderStatusByName(String name) {
        return orderStatusRepo.findByStatusName(name);
    }


    public OrderStatus getOrderStatusById(Long id) {
        return orderStatusRepo.findById(id).orElseThrow();
    }


    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }


    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepo.findByOrderStatus(status);
    }


    public void saveOrder(Order order) {
        orderRepo.save(order);
    }


    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(orderStatus);
        orderRepo.save(order);
    }


    public void updateOrderChef(Order order, Staff chef){
        order.setChef(chef);
        orderRepo.save(order);
    }


    public void updateOrdersChef(List<Order> orderList, Staff chef){
        for (Order order : orderList) {
            order.setChef(chef);
        }
        orderRepo.saveAll(orderList);
    }


    public boolean areOrdersReady(Long receiptId) {
        Receipt currentReceipt = receiptRepo.findById(receiptId).orElseThrow();
        List<Order> orders = orderRepo.findByReceipt(currentReceipt);
        return orders.stream().allMatch(order -> order.getOrderStatus().getStatusName().equals("READY"));
    }
}
