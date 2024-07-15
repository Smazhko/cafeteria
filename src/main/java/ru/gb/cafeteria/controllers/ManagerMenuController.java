package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.domain.MenuItem;
import ru.gb.cafeteria.services.FoodGroupService;
import ru.gb.cafeteria.services.MenuService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/manager/menu")
public class ManagerMenuController {

    private MenuService menuService;
    private FoodGroupService foodGroupService;

    @GetMapping
    public String showMenu(Model model) {
        List<MenuItem> menuItems = menuService.getNonArchivedMenuItems();
        Map<String, List<MenuItem>> groupedMenuItems = menuService.getSortedMenu(menuItems);
        model.addAttribute("groupedMenuItems", groupedMenuItems);
        // System.out.println(groupedMenuItems);
        model.addAttribute("search", "");
        model.addAttribute("searchIsEmpty", false);
        return "manager/menu";
    }

    @GetMapping("/search")
    public String searchMenuItem(@RequestParam("search") String search, Model model) {
        // несмотря на то, что поиск ограничен по количеству введенных символов в форме
        // повторно проводим проверку на длину строки
        List<MenuItem> searchResult;
        Boolean searchIsEmpty;
        if (search.trim().length() > 1) {
            searchResult = menuService.searchNonArchivedMenuItems(search);  // searchMenuItems(searchLine);
            model.addAttribute("searchIsEmpty", searchResult.isEmpty());
            if (searchResult.isEmpty())
                searchResult = menuService.getNonArchivedMenuItems();
        } else {
            searchResult = menuService.getNonArchivedMenuItems();
            model.addAttribute("searchIsEmpty", false);
        }
        Map<String, List<MenuItem>> groupedMenuItems = menuService.getSortedMenu(searchResult);
        model.addAttribute("search", search);
        model.addAttribute("groupedMenuItems", groupedMenuItems);
        return "manager/menu";
    }

    @GetMapping("/reset-search")
    public String resetSearch(Model model) {
        model.addAttribute("search", "");
        model.addAttribute("searchIsEmpty", false);
        return "redirect:/manager/menu";
    }

    @GetMapping("/add")
    public String addMenuItemForm(Model model) {
        List<FoodGroup> foodGroups = foodGroupService.getAllFoodGroups();
        model.addAttribute("foodGroups", foodGroups);
        model.addAttribute("newItem", new MenuItem());
        return "manager/menu-add";
    }

    // добавляем новый элемент меню
    @PostMapping(value="/add", consumes = "multipart/form-data")
    public String addMenuItem(@ModelAttribute MenuItem newItem,
                              @RequestParam("image") MultipartFile imageFile,
                              Model model) {
        // сохраняем картинку в папку и в БД
        if (!imageFile.isEmpty()) {
            try {
                String fileName = menuService.loadImage(imageFile);
                 newItem.setImageURL("/img/food/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
                return "manager/menu";
            }
        }
        FoodGroup foodGroup = foodGroupService.getFoodGroupById(newItem.getFoodGroup().getGroupId());
        newItem.setFoodGroup(foodGroup);
        // System.out.println("новое блюдо - " + newItem);
        menuService.addNewMenuItem(newItem);
        return "redirect:/manager/menu";
    }


    @GetMapping("/update/{id}")
    public String showUpdateMenuItemForm(@PathVariable Long id, Model model) {
        MenuItem itemToUpdate = menuService.getMenuItemById(id);
        List<FoodGroup> foodGroups = foodGroupService.getAllFoodGroups();
        model.addAttribute("itemToUpdate", itemToUpdate);
        model.addAttribute("foodGroups", foodGroups);
        return "manager/menu-update";
    }

    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public String updateMenuItem(@ModelAttribute MenuItem itemToUpdate,
                                 @RequestParam("image") MultipartFile imageFile,
                                 Model model) {
        // сохраняем картинку в папку и в БД
        if (!imageFile.isEmpty()) {
            try {
                String fileName = menuService.loadImage(imageFile);
                itemToUpdate.setImageURL("/img/food/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
                return "redirect:/manager/menu";
            }
        }

        FoodGroup foodGroup = foodGroupService.getFoodGroupById(itemToUpdate.getFoodGroup().getGroupId());
        itemToUpdate.setFoodGroup(foodGroup);
        // System.out.println(itemToUpdate);
        menuService.updateMenuItem(itemToUpdate);
        return "redirect:/manager/menu";
    }


    @PostMapping("/toggle-active/{id}")
    @ResponseBody
    public void toggleActive(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        boolean isActive = payload.get("active");
        System.out.println("ИЗМЕНЕНИЕ АКТИВНОСТИ - " + isActive);
        menuService.updateMenuItemStatus(id, isActive);
    }


    @PostMapping("/delete/{id}")
    public String archiveMenuItem(@PathVariable Long id) {
        menuService.archiveMenuItem(id);
        return "redirect:/manager/menu";
    }

    @GetMapping("/archive")
    public String showArchive (Model model){
        List<MenuItem> menuItems = menuService.getArchivedMenuItems();
        Map<String, List<MenuItem>> groupedMenuItems = menuService.getSortedMenu(menuItems);
        model.addAttribute("groupedMenuItems", groupedMenuItems);
        return "manager/menu-archive";
    }

    @GetMapping("/restore/{id}")
    public String restoreFromArchive(@PathVariable Long id){
        menuService.restoreItemFromArchiveById(id);
        return "redirect:/manager/menu/archive";
    }

}
