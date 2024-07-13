package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.domain.MenuItem;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByFoodGroup(FoodGroup foodGroup);

    List<MenuItem> findAllByArchivedFalse();

    @Query("SELECT m FROM MenuItem m WHERE m.archived = false " +
            "AND (LOWER(m.name) LIKE %:search% OR LOWER(m.description) LIKE %:search%)")
    List<MenuItem> searchNonArchivedMenuItems(@Param("search") String search);

    Integer countByFoodGroup(FoodGroup group);
}

