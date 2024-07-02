package ru.gb.cafeteria.domain;

import jakarta.persistence.*;
import lombok.Data;
import ru.gb.cafeteria.security.domain.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Data
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String post;

    @Column(nullable = false)
    private String phone;

    @Column(name = "date_begin", nullable = false)
    private LocalDate dateBegin;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", name='" + name + '\'' +
                ", post='" + post + '\'' +
                ", phone='" + phone + '\'' +
                ", dateBegin=" + dateBegin +
                ", dateEnd=" + dateEnd +
                ", salary=" + salary +
                //", user=" + user.getUsername() + // Avoid calling user.toString()
                '}';
    }
}
