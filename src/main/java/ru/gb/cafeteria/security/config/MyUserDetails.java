package ru.gb.cafeteria.security.config;

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
    public boolean isEnabled() { // Проверка, что аккаунт действителен до даты увольнения
        LocalDate dateEnd = user.getStaff().getDateEnd();
        return user.getEnabled() && (dateEnd == null || !LocalDate.now().isAfter(dateEnd)); }
}