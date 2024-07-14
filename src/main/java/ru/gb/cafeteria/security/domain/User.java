package ru.gb.cafeteria.security.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.cafeteria.domain.Staff;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean enabled;

    @OneToOne(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Staff staff;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id:", id)
                .append(", username:", username)
                .append(", password:", password)
                .append(", role:", role)
                .append(", enabled:", enabled)
                .append(", staff", staff.getName())
                .toString();
    }
}
