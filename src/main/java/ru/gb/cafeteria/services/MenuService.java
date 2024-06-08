package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.domain.MenuItem;
import ru.gb.cafeteria.repository.FoodGroupRepository;
import ru.gb.cafeteria.repository.MenuItemRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MenuService {
    private MenuItemRepository menuRepo;
    private FoodGroupRepository foodGroupRepo;

    public List<MenuItem> getAllMenuItems() {
        return menuRepo.findAll().stream()
                .sorted(Comparator
                        .comparing((MenuItem item) -> item.getFoodGroup().getGroupId())
                        .thenComparing(MenuItem::getName)
                        .thenComparing(MenuItem::getActive).reversed())
                .collect(Collectors.toList());
    }

    public List<MenuItem> getAllActiveMenuItems() {
        return menuRepo.findAll().stream()
                .filter(MenuItem::getActive)
                .sorted(Comparator
                        .comparing((MenuItem item) -> item.getFoodGroup().getGroupId())
                        .thenComparing(MenuItem::getName))
                .collect(Collectors.toList());
    }

    public List<FoodGroup> getAllFoodGroups() {
        return foodGroupRepo.findAll();
    }

    public List<MenuItem> getMenuItemByName(String name) {
        return menuRepo.findByName(name);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuRepo.findById(id).orElseThrow();
    }

    public void addNewMenuItem(MenuItem newItem){
        if (!newItem.getName().isEmpty()
        && newItem.getFoodGroup() != null)
            menuRepo.save(newItem);
    }

    public MenuItem updateMenuItem (MenuItem updatedMenuItem) {
        return menuRepo.save(updatedMenuItem);
    }

    public MenuItem updateMenuItemStatus(Long id, boolean active) {
        Optional<MenuItem> optionalMenuItem = menuRepo.findById(id);
        if (optionalMenuItem.isPresent()) {
            MenuItem menuItem = optionalMenuItem.get();
            menuItem.setActive(active);
            return menuRepo.save(menuItem);
        } else {
            throw new RuntimeException("MenuItem not found");
        }
    }
}
