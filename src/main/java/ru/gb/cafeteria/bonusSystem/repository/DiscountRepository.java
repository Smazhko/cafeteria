package ru.gb.cafeteria.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.bonusSystem.domain.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
