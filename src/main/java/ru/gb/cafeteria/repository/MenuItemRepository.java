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
    List<MenuItem> findByName(String name);

    List<MenuItem> findByFoodGroup(FoodGroup foodGroup);

    List<MenuItem> findAllByArchivedFalse();

    @Query("SELECT m FROM MenuItem m WHERE m.archived = false AND (m.name LIKE %:search% OR m.description LIKE %:search%)")
    List<MenuItem> searchNonArchivedMenuItems(@Param("search") String search);

    Integer countByFoodGroup(FoodGroup group);
}

/*
Если нужны более сложные запросы, которые не могут быть легко выражены с помощью методов
на основе соглашений об именовании, также можно определить собственные методы в репозитории,
используя @Query и JPQL (Java Persistence Query Language). Например:

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT m FROM MenuItem m WHERE m.name = :name")
    List<MenuItem> findByNameCustom(@Param("name") String name);
}
 */
