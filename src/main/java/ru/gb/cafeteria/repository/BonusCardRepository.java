package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.BonusCard;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonusCardRepository extends JpaRepository<BonusCard, Long> {
    List<BonusCard> findByClientName(String name);

    List<BonusCard> findByClientPhone(String name);

    boolean existsByClientPhone(String clientPhone);
}
