package ru.gb.cafeteria.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.Staff;
import ru.gb.cafeteria.security.domain.Role;
import ru.gb.cafeteria.security.domain.User;
import ru.gb.cafeteria.security.services.RoleService;
import ru.gb.cafeteria.services.StaffService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager/staff")
@AllArgsConstructor
public class ManagerStaffController {

    private final RoleService roleService;
    private final StaffService staffService;


    @GetMapping
    public String getAllStaff(Model model, @RequestParam(required = false) Long roleId) {
        List<Role> roles = staffService.getAllRoles();
        List<Staff> staffList;

        if (roleId != null && roleId != 0) {
            Role role = staffService.getRoleById(roleId);
            staffList = staffService.getStaffByRole(role);
        } else {
            staffList = staffService.getAllStaff();
        }

        model.addAttribute("roles", roles);
        model.addAttribute("staffList", staffList);
        model.addAttribute("selectedRole", roleId);
        return "/manager/staff";
    }

    @PostMapping("/inactivate")
    public String inactivateStaff(@RequestParam Long staffId) {
        staffService.inactivateStaffById(staffId, LocalDate.now());
        return "redirect:/manager/staff";
    }

    @PostMapping("/reactivate")
    public String reactivateStaff(@RequestParam Long staffId) {
        staffService.reactivateStaffById(staffId);
        return "redirect:/manager/staff";
    }

    @GetMapping("/add")
    public String addStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roles", staffService.getAllRoles());
        model.addAttribute("today", LocalDate.now());
        return "manager/staff-add";
    }

    @PostMapping("/add")
    public String addStaff(@Valid @ModelAttribute Staff staff, BindingResult bindingResult,
                           @RequestParam String username, @RequestParam String password,
                           @RequestParam Long roleId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", staffService.getAllRoles());
            return "manager/staff-add";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(staffService.getRoleById(roleId));
        user.setEnabled(true);
        user = staffService.createUser(user, roleId);

        staff.setUser(user);
        staffService.addStaff(staff);

        return "redirect:/manager/staff";
    }

    @GetMapping("/update/{staffId}")
    public String editStaffForm(@PathVariable Long staffId, Model model) {
        Staff staff = staffService.getStaffById(staffId);
        model.addAttribute("staff", staff);
        model.addAttribute("roles", staffService.getAllRoles());

        System.out.println("GET edit form " + staff);
        return "manager/staff-update";
    }

    @PostMapping("/update")
    public String editStaff(@ModelAttribute Staff staff, @RequestParam String username, @RequestParam(required = false) String password, @RequestParam Long roleId) {
        System.out.println("staff " + staff);
        System.out.println("username " + username);
        System.out.println("password " + password);
        System.out.println("roleId " + roleId);

        staffService.updateStaffDetails(staff, username, password, roleId);

        return "redirect:/manager/staff";
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = staffService.usernameExists(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
