package ru.gb.cafeteria.services;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.MenuItem;
import ru.gb.cafeteria.domain.Order;
import ru.gb.cafeteria.domain.Receipt;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BasketService {

    private final MenuService menuService;

    public void addToBasket(Long menuItemId, HttpSession session) {
        MenuItem menuItem = menuService.getMenuItemById(menuItemId);
        BasketDTO basket = (BasketDTO) session.getAttribute("basket");
        if (basket == null) {
            basket = new BasketDTO();
        }

        Optional<BasketItemDTO> optionalBasketItem = basket.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (optionalBasketItem.isPresent()) {
            BasketItemDTO basketItem = optionalBasketItem.get();
            basketItem.setQuantity(basketItem.getQuantity() + 1);
        } else {
            BasketItemDTO newItem = new BasketItemDTO(menuItem, 1);
            basket.getItems().add(newItem);
        }

        session.setAttribute("basket", basket);
    }

    public BasketDTO getBasket(HttpSession session) {
        BasketDTO basket = (BasketDTO) session.getAttribute("basket");
        if (basket == null) {
            basket = new BasketDTO();
            session.setAttribute("basket", basket);
        }
        return basket;
    }

    public void updateQuantity(Long menuItemId, int quantity, HttpSession session) {
        BasketDTO basket = (BasketDTO) session.getAttribute("basket");
        if (basket == null) {
            basket = new BasketDTO();
        }

        Optional<BasketItemDTO> optionalBasketItem = basket.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (optionalBasketItem.isPresent()) {
            BasketItemDTO basketItem = optionalBasketItem.get();
            if (quantity > 0) {
                basketItem.setQuantity(quantity);
            } else {
                basket.getItems().remove(basketItem);
            }
        } else {
            MenuItem menuItem = menuService.getMenuItemById(menuItemId);
            BasketItemDTO newItem = new BasketItemDTO(menuItem, quantity);
            basket.getItems().add(newItem);
        }

        session.setAttribute("basket", basket);
    }

    public void clearBasket(HttpSession session) {
        session.removeAttribute("basket");
    }
}
