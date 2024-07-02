package ru.gb.cafeteria.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.bonusSystem.domain.BonusTransaction;

@Repository
public interface BonusTransactionRepository extends JpaRepository<BonusTransaction, Long> {
}
