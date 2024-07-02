package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.FoodGroup;

@Repository
public interface FoodGroupRepository extends JpaRepository<FoodGroup, Long> {
    FoodGroup findByPosition(Integer position);

    boolean existsByName(String name);
}
