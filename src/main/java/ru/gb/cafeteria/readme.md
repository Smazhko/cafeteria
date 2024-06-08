Для реализации функционала, который позволяет повару активировать или деактивировать блюда в меню, наилучшей практикой будет создать метод в сервисе, который будет обрабатывать изменение поля `active` в классе `MenuItem`. Репозиторий будет использоваться для выполнения операции обновления в базе данных.

Вот пример того, как это можно реализовать:

### 1. Определите сущность MenuItem

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean active;

    // Getters and setters
}
```

### 2. Создайте репозиторий для MenuItem

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
```

### 3. Создайте сервис для MenuItem

В сервисе создайте метод для обновления поля `active`.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem updateMenuItemStatus(Long id, boolean active) {
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);
        if (optionalMenuItem.isPresent()) {
            MenuItem menuItem = optionalMenuItem.get();
            menuItem.setActive(active);
            return menuItemRepository.save(menuItem);
        } else {
            throw new RuntimeException("MenuItem not found");
        }
    }
}
```

### 4. Создайте контроллер для обработки запросов

Контроллер будет принимать запросы от повара и вызывать метод сервиса для обновления статуса блюда.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PutMapping("/{id}/status")
    public MenuItem updateMenuItemStatus(@PathVariable Long id, @RequestParam boolean active) {
        return menuItemService.updateMenuItemStatus(id, active);
    }
}
```

### 5. Создайте страницу и контроллер для повара

Создайте HTML страницу с использованием Thymeleaf, чтобы повар мог видеть список блюд и изменять их статус.

**menu.html:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Menu Management</title>
</head>
<body>
<h1>Menu Management</h1>
<table>
    <thead>
    <tr>
        <th>Name</th>
        <th>Status</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <tr th:each="menuItem : ${menuItems}">
        <td th:text="${menuItem.name}">Name</td>
        <td th:text="${menuItem.active ? 'Active' : 'Inactive'}">Status</td>
        <td>
            <form th:action="@{/api/menu/{id}/status(id=${menuItem.id})}" method="post">
                <input type="hidden" name="active" th:value="${!menuItem.active}"/>
                <button type="submit" th:text="${menuItem.active ? 'Deactivate' : 'Activate'}"></button>
            </form>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
```

**MenuController.java:**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        model.addAttribute("menuItems", menuItemService.findAllMenuItems());
        return "menu";
    }
}
```

### 6. Обновите сервис для получения всех блюд

Добавьте метод для получения всех блюд в сервисе.

```java
import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem updateMenuItemStatus(Long id, boolean active) {
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);
        if (optionalMenuItem.isPresent()) {
            MenuItem menuItem = optionalMenuItem.get();
            menuItem.setActive(active);
            return menuItemRepository.save(menuItem);
        } else {
            throw new RuntimeException("MenuItem not found");
        }
    }

    public List<MenuItem> findAllMenuItems() {
        return menuItemRepository.findAll();
    }
}
```

### Заключение

Таким образом, вы создаете метод в сервисе для обновления поля `active` у блюда и вызываете его из контроллера. Репозиторий отвечает за взаимодействие с базой данных, а сервис обрабатывает бизнес-логику. Контроллеры предоставляют интерфейс для взаимодействия с пользователями.

*****
Чтобы избежать падения приложения и корректно обработать ситуацию, когда элемент меню не найден, можно вернуть специальный HTTP-статус код, например 404 (Not Found). Для этого в сервисе можно выбросить специальное исключение, а в контроллере обработать его с помощью `@ExceptionHandler`.

Вот как можно это сделать:

### 1. Создайте специальное исключение

Создайте собственное исключение, которое будет выбрасываться, когда элемент меню не найден.

```java
public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
```

### 2. Обновите сервис для использования нового исключения

Измените метод в сервисе для выбрасывания `MenuItemNotFoundException`.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem updateMenuItemStatus(Long id, boolean active) {
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);
        if (optionalMenuItem.isPresent()) {
            MenuItem menuItem = optionalMenuItem.get();
            menuItem.setActive(active);
            return menuItemRepository.save(menuItem);
        } else {
            throw new MenuItemNotFoundException("MenuItem not found with id: " + id);
        }
    }

    public List<MenuItem> findAllMenuItems() {
        return menuItemRepository.findAll();
    }
}
```

### 3. Обновите контроллер для обработки исключения

В контроллере добавьте метод для обработки `MenuItemNotFoundException`.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PutMapping("/{id}/status")
    public MenuItem updateMenuItemStatus(@PathVariable Long id, @RequestParam boolean active) {
        return menuItemService.updateMenuItemStatus(id, active);
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<String> handleMenuItemNotFoundException(MenuItemNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
```

### Обработка ошибки на странице

Для обработки ошибки на странице можно создать отдельный контроллер, который будет отображать страницу ошибки. Однако, если вы хотите обрабатывать ошибки глобально и отображать определенную страницу для всех 404 ошибок, можно создать глобальный контроллер ошибок.

```java
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("errorCode", statusCode);
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "Page not found");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorMessage", "Access denied");
            } else {
                model.addAttribute("errorMessage", "An unexpected error occurred");
            }
        }
        return "error";
    }
}
```

Создайте шаблон `error.html` для отображения ошибок:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Error</title>
</head>
<body>
<h1>Error</h1>
<p th:text="'Error code: ' + ${errorCode}"></p>
<p th:text="'Error message: ' + ${errorMessage}"></p>
</body>
</html>
```

Теперь, если элемент меню не найден, сервис выбросит `MenuItemNotFoundException`, контроллер перехватит его и вернет 404 статус. Приложение не упадет, а пользователь получит корректное сообщение об ошибке.