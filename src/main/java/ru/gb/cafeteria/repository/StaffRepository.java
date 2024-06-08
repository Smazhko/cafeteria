package ru.gb.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.cafeteria.domain.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
}
