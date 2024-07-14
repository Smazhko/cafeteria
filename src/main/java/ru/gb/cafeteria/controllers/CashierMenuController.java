package ru.gb.cafeteria.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;
import ru.gb.cafeteria.services.BasketService;
import ru.gb.cafeteria.services.MenuService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/cashier/menu")
public class CashierMenuController {

    private MenuService menuService;
    private BasketService basketService;

    // При использовании th:object на форме, Thymeleaf ожидает объект, который поддерживает доступ к своим полям,
    // а не к элементам списка напрямую.
    // Для решения этой проблемы необходимо создать обертку для списка объектов и использовать ее в форме.
    // Поэтому воспользуемся объектом-прослойкой (BasketDTO) для передачи данных в HTML форму для формирования заказа.
    private void initializeModel(Model model, HttpSession session, List<MenuItem> menuItems) {
        Map<String, List<MenuItem>> groupedMenuItems = menuService.getSortedMenu(menuItems);
        model.addAttribute("groupedMenuItems", groupedMenuItems);

        // Добавление корзины в модель
        BasketDTO basket = basketService.getBasket(session);
        model.addAttribute("basket", basket);
        model.addAttribute("totalCost", basket.getTotalCost());

        // Создание объекта для хранения количества каждого пункта меню
        Map<Long, Integer> menuItemQuantities = new HashMap<>();
        for (MenuItem item : menuItems) {
            menuItemQuantities.put(item.getId(), 0); // Инициализация нулями
        }
        for (BasketItemDTO basketItem : basket.getItems()) {
            menuItemQuantities.put(basketItem.getMenuItem().getId(), basketItem.getQuantity());
        }
        model.addAttribute("menuItemQuantities", menuItemQuantities);
    }

    @TrackUserAction
    @GetMapping
    public String getMenuPage(Model model, HttpSession session) {
        List<MenuItem> menuItems = menuService.getNonArchivedMenuItems();
        initializeModel(model, session, menuItems);
        return "cashier/menu";
    }

    @GetMapping("/search")
    public String searchMenuItem(@RequestParam("search") String search, Model model, HttpSession session) {
        List<MenuItem> searchResult;
        if (search.trim().length() > 1) {
            searchResult = menuService.searchNonArchivedMenuItems(search);
            model.addAttribute("searchIsEmpty", searchResult.isEmpty());
            if (searchResult.isEmpty()) {
                searchResult = menuService.getNonArchivedMenuItems();
            }
            model.addAttribute("search", search);
        } else {
            searchResult = menuService.getNonArchivedMenuItems();
            model.addAttribute("search", "");
            model.addAttribute("searchIsEmpty", false);
        }
        initializeModel(model, session, searchResult);
        return "cashier/menu";
    }

    @GetMapping("/reset-search")
    public String resetSearch(HttpSession session, Model model) {
        List<MenuItem> menuItems = menuService.getNonArchivedMenuItems();
        initializeModel(model, session, menuItems);
        return "redirect:/cashier/menu";
    }

    // обновляем объект корзины, если пункт меню активен
    @TrackUserAction
    @PostMapping("/updateBasket")
    @ResponseBody
    public BasketDTO updateBasket(@RequestBody BasketItemDTO basketItem, HttpSession session) {
        if (menuService.isMenuItemActiveById(basketItem.getMenuItem().getId())) {
            basketService.updateQuantity(basketItem.getMenuItem().getId(), basketItem.getQuantity(), session);
        }
        return basketService.getBasket(session);
    }

    @PostMapping("/resetBasket")
    @ResponseBody
    public BasketDTO resetBasket(HttpSession session) {
        basketService.clearBasket(session);
        return basketService.getBasket(session);
    }

    @GetMapping("/checkMenuItemStatus")
    @ResponseBody
    @TrackUserAction
    public boolean checkMenuItemStatus(@RequestParam Long menuItemId) {
        return menuService.isMenuItemActiveById(menuItemId);
    }

}