package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.Staff;
import ru.gb.cafeteria.repository.StaffRepository;
import ru.gb.cafeteria.security.domain.Role;
import ru.gb.cafeteria.security.domain.User;
import ru.gb.cafeteria.security.repository.RoleRepository;
import ru.gb.cafeteria.security.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StaffService {

    private final StaffRepository staffRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public Staff addStaff(Staff newPerson) {
        return staffRepo.save(newPerson);
    }

    public void inactivateStaffById(Long staffId, LocalDate dateEnd) {
        Staff staff = staffRepo.findById(staffId).orElseThrow();
        staff.setDateEnd(dateEnd);
        staffRepo.save(staff);
        User user = staff.getUser();
        user.setEnabled(false);
        userRepo.save(user);
    }

    public void reactivateStaffById(Long staffId) {
        Staff staff = staffRepo.findById(staffId).orElseThrow();
        staff.setDateBegin(LocalDate.now());
        staff.setDateEnd(null);
        staffRepo.save(staff);
        User user = staff.getUser();
        user.setEnabled(true);
        userRepo.save(user);
    }

    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User createUser(User newUser, Long roleId) {
        Role role = roleRepo.findById(roleId).orElseThrow();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(role);
        return userRepo.save(newUser);
    }

    public List<Staff> getStaffByRole(Role role) {
        return staffRepo.findAll().stream()
                .filter(staff -> staff.getUser().getRole().equals(role))
                .collect(Collectors.toList());
    }

    // получаем все роли для редактирования персонала. роли НЕперсонала исключаем
    public List<Role> getAllRoles() {
        return roleRepo.findAll().stream()
                .filter(role -> !role.getName().equals("ROLE_ADMIN") && !role.getName().equals("ROLE_CLIENT"))
                .collect(Collectors.toList());
    }


    public Staff getStaffById(Long id) {
        return staffRepo.findById(id).orElseThrow();
    }

    public Staff findStaffByUsername(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return staffRepo.findByUser(user).orElseThrow();
    }

    public Role getRoleById(Long id) {
        return roleRepo.findById(id).orElseThrow();
    }

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    // обновляем данные по сотруднику.
    // а также по его аккаунту. если выставили дату увольнения, то аккаунт деактивируется, чтоб по нему нельзя было зайти
    public void updateStaffDetails(Staff staff, String username, String password, Long roleId) {
        Staff existingStaff = staffRepo.findById(staff.getStaffId()).orElseThrow();
        User user = existingStaff.getUser();
        user.setUsername(username);
        if (password != null && !password.isEmpty()) {
            user.setPassword(encodePassword(password));
        }
        user.setRole(getRoleById(roleId));
        updateUser(user);

        existingStaff.setName(staff.getName());
        existingStaff.setPhone(staff.getPhone());
        existingStaff.setPost(staff.getPost());
        existingStaff.setSalary(staff.getSalary());
        existingStaff.setDateBegin(staff.getDateBegin());
        existingStaff.setDateEnd(staff.getDateEnd());
        if (staff.getDateEnd() != null && LocalDate.now().isAfter(staff.getDateEnd())) {
            user.setEnabled(false);
        } else {
            user.setEnabled(true);
        }
        existingStaff.setUser(user);
        staffRepo.save(existingStaff);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean usernameExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }
}
