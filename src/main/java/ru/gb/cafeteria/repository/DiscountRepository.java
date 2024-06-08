package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
