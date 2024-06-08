package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.Order;
import ru.gb.cafeteria.domain.OrderStatus;
import ru.gb.cafeteria.domain.Receipt;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    List<Order> findByReceipt(Receipt receipt);
}
