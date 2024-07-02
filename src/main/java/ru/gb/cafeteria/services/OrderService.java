package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.*;
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


    public List<Order> createOrderList(Receipt receipt, List<MenuItem> menuItemList) {
        OrderStatus formedStatus = getOrderStatusById(1L);
        List<Order> orderList = new ArrayList<>();

        for (MenuItem menuItem : menuItemList) {
            Order order = new Order();
            BigDecimal price = menuItem.getPrice();
            Integer quantity = menuItem.getQuantity();
            BigDecimal sum = price.multiply(BigDecimal.valueOf(quantity));
            order.setMenuItem(menuItem);
            order.setQuantity(quantity);
            order.setPrice(price);
            order.setSum(sum);
            order.setOrderStatus(formedStatus);
            order.setReceipt(receipt);
            orderList.add(order);
        }
        orderRepo.saveAll(orderList);
        return orderList;
    }


    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(orderStatus);
        orderRepo.save(order);
    }


    public void updateOrdersStatuses(List<Order> orderList, OrderStatus orderStatus) {
        for (Order order : orderList) {
            order.setOrderStatus(orderStatus);
        }
        orderRepo.saveAll(orderList);
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
