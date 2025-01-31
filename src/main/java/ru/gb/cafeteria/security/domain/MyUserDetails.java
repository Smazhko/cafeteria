package ru.gb.cafeteria.security.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.cafeteria.domain.Staff;
import ru.gb.cafeteria.security.domain.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {

    private final User user;

    public MyUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    public Staff getStaff() { return user.getStaff(); }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() {return true; }

    @Override
    // Проверка, что аккаунт действителен в диапазон дат между приёмом и увольнением
    public boolean isEnabled() {
        if (user.getStaff() == null) {
            return true;
        } else {
            LocalDate dateBegin = user.getStaff().getDateBegin();
            LocalDate dateEnd = user.getStaff().getDateEnd();
            LocalDate today = LocalDate.now();
            return user.getEnabled() && !today.isBefore(dateBegin) && (dateEnd == null || !today.isAfter(dateEnd));
        }
    }

}