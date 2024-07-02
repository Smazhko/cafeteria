package ru.gb.cafeteria.security.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.security.domain.Role;
import ru.gb.cafeteria.security.repository.RoleRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;

    public Role getRoleByName(String name) {
        return roleRepo.findByName(name).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    public void createRole(Role role) {
        roleRepo.save(role);
    }
}