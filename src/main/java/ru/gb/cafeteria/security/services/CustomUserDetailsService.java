package ru.gb.cafeteria.security.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.security.domain.Role;
import ru.gb.cafeteria.security.domain.User;
import ru.gb.cafeteria.security.repository.RoleRepository;
import ru.gb.cafeteria.security.repository.UserRepository;
import ru.gb.cafeteria.security.config.MyUserDetails;
import ru.gb.cafeteria.services.StaffService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsername(username);
        return user.map(MyUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException(username + ": There is not such user in REPO"));
    }

}