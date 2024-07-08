package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.DiscountType;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountType, Long> {
}
