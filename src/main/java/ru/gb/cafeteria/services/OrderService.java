package ru.gb.cafeteria.services;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;
import ru.gb.cafeteria.repository.*;
import ru.gb.cafeteria.security.domain.User;
import ru.gb.cafeteria.security.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepo;
    private OrderStatusRepository orderStatusRepo;
    private ReceiptStatusRepository receiptStatusRepo;
    private UserRepository userRepo;
    private StaffRepository staffRepo;


    public OrderStatus getOrderStatusByName(String name) {
        return orderStatusRepo.findByStatusName(name);
    }


    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }


    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepo.findByOrderStatus(status);
    }


    public void updateOrderStatus(Long orderId, OrderStatus orderStatus, HttpSession session) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(orderStatus);

        // Получение текущего пользователя
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("Unable to get current user");
        }
        Staff staff = staffRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        order.setChef(staff);
        updateReceiptStatus(order.getReceipt());
        orderRepo.save(order);
    }


    // после изменения статуса каждого заказа обновляем статус чека
    // получаем чек, получаем список его заказов.
    // если в списке заказов текущего чека все заказы со статусом READY, то чек меняет статус на CLOSED
    public void updateReceiptStatus(Receipt receipt) {
        ReceiptStatus closedStatus = receiptStatusRepo.findByStatusName("CLOSED");
        List<Order> orders = orderRepo.findByReceipt(receipt);
        if (orders.stream().allMatch(order -> order.getOrderStatus().getStatusName().equals("READY")))
            receipt.setReceiptStatus(closedStatus);
    }
}
