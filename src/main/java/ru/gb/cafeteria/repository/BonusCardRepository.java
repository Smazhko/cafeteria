package ru.gb.cafeteria.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.BonusCard;

import java.util.List;

@Repository
public interface BonusCardRepository extends JpaRepository<BonusCard, Long> {

    boolean existsByClientPhone(String clientPhone);

    List<BonusCard> findByClientPhone(String name);

    Page<BonusCard> findByClientPhoneContaining(String phone, Pageable pageable);

    long countByDiscountType_DiscountId(Long id);
}
