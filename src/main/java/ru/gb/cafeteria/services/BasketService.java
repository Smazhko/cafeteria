package ru.gb.cafeteria.services;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.domain.MenuItem;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BasketService {

    private final MenuService menuService;

    // получить корзину
    public BasketDTO getBasket(HttpSession session) {
        BasketDTO basket = (BasketDTO) session.getAttribute("basket");
        if (basket == null) {
            basket = new BasketDTO();
            session.setAttribute("basket", basket);
        }
        return basket;
    }


    // метод обновления корзины.
    // получаем объект из сессии - если он пуст, значит создаём новый.
    // проверяем корзину - если добавляемый объект в ней уже имеется, то увеличиваем его количество,
    // если количество добавляемого объекта = 0, то удаляем его из корзины.
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


    // очистить корзину
    public void clearBasket(HttpSession session) {
        session.removeAttribute("basket");
    }
}
