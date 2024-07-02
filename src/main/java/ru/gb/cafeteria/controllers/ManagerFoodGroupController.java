package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.exceptions.FoodGroupDeleteException;
import ru.gb.cafeteria.services.FoodGroupService;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/manager/food-groups")
public class ManagerFoodGroupController {
    private FoodGroupService foodGroupService;

    @GetMapping
    public String listFoodGroups(Model model, RedirectAttributes attributes) {
        model.addAttribute("foodGroups", foodGroupService.getAllFoodGroups());
        Map<Long, Integer> foodCounts = foodGroupService.getFoodCountsByGroup();
        model.addAttribute("foodCounts", foodCounts);
        return "manager/food-groups";
    }

    @PostMapping("/add")
    public String addFoodGroup(@ModelAttribute FoodGroup foodGroup) {
        foodGroupService.addNewFoodGroup(foodGroup);
        return "redirect:/manager/food-groups";
    }

    @PostMapping("/update")
    public String updateFoodGroup(@ModelAttribute FoodGroup foodGroup) {
        foodGroupService.updateFoodGroup(foodGroup);
        return "redirect:/manager/food-groups";
    }

    @PostMapping("/delete/{id}")
    public String deleteFoodGroup(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            foodGroupService.deleteFoodGroupById(id);
        } catch (FoodGroupDeleteException e) {
            attributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/food-groups";
    }

    @PostMapping("/move-up/{id}")
    public String moveUp(@PathVariable Long id) {
        foodGroupService.changeFoodGroupOrder(id, true);
        return "redirect:/manager/food-groups";
    }

    @PostMapping("/move-down/{id}")
    public String moveDown(@PathVariable Long id) {
        foodGroupService.changeFoodGroupOrder(id, false);
        return "redirect:/manager/food-groups";
    }
}
