package ru.gb.cafeteria.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.domain.MenuItem;
import ru.gb.cafeteria.repository.FoodGroupRepository;
import ru.gb.cafeteria.repository.MenuItemRepository;
import ru.gb.cafeteria.repository.OrderRepository;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MenuService {
    public static final String FOOD_DEFAULT_PNG = "/img/food/default.png";
    private static final String IMAGE_UPLOAD_DIR = "src/main/resources/static/img/food/";

    private MenuItemRepository menuRepo;
    private FoodGroupRepository groupRepo;
    private OrderRepository orderRepo;


    // Получить все пункты меню, кроме архивированных (ACTIVE и INACTIVE)
    public List<MenuItem> getNonArchivedMenuItems() {
        return menuRepo.findAllByArchivedFalse();
    }


    // Получить все активные пункты меню
    public List<MenuItem> getActiveMenuItems() {
        return menuRepo.findAllByArchivedFalse().stream()
                .filter(MenuItem::getActive)
                .collect(Collectors.toList());
    }

    public Boolean isMenuItemActiveById(Long id) {
        MenuItem item = menuRepo.findById(id).orElseThrow();
        return item.getActive();
    }


    // Получить все архивированные пункты меню, отсортированные по алфавиту
    public List<MenuItem> getArchivedMenuItems() {
        return menuRepo.findAll().stream()
                .filter(MenuItem::getArchived)
                .sorted(Comparator.comparing(MenuItem::getName))
                .collect(Collectors.toList());
    }


    // создание MAP и сортировка по ее по ключу - по номерам foodGroup.number, чтобы было удобно компоновать меню
    // внутри каждой группы происходит сортировка по названию (по алфавиту) и по активности блюда
    public LinkedHashMap<String, List<MenuItem>> getSortedMenu(List<MenuItem> menuItems){
        return menuItems.stream()
                .collect(Collectors.groupingBy(item -> item.getFoodGroup().getName()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(
                        Comparator.comparing(key -> menuItems.stream()
                                .filter(item -> item.getFoodGroup().getName().equals(key))
                                .findFirst()
                                .get()
                                .getFoodGroup()
                                .getPosition())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(MenuItem::getName))
                                .sorted(Comparator.comparing(MenuItem::getActive).reversed())
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }


    @TrackUserAction
    // поиск по меню - для поисковой строки
    public List<MenuItem> searchNonArchivedMenuItems(String search) {
        String searchLowerCase = search.toLowerCase();
        return menuRepo.searchNonArchivedMenuItems(searchLowerCase);
    }


    public MenuItem getMenuItemById(Long id) {
        return menuRepo.findById(id).orElseThrow();
    }


    // сохранение НОВОГО пункта меню с некоторыми автоматическими изменениями
    // - specialPrice (если пусто - то равна обычной цене)
    // - если картинка не назначена, то выставляется дефолтная
    public void addNewMenuItem(MenuItem newItem) {
        if (!newItem.getName().isEmpty()
                && newItem.getFoodGroup() != null
                && newItem.getActive() != null
        ) {
            if (newItem.getSpecialPrice() == null)
                newItem.setSpecialPrice(newItem.getPrice());
            if (newItem.getImageURL() == null || newItem.getImageURL().trim().isEmpty())
                newItem.setImageURL(FOOD_DEFAULT_PNG);
            newItem.setArchived(false);
            // в модели сохраняется только ID группы, поэтому, чтобы класс был полным, сохраняем группы заново
            FoodGroup foodGroup = groupRepo.findById(newItem.getFoodGroup().getGroupId()).orElseThrow();
            newItem.setFoodGroup(foodGroup);
            newItem.setStatusChangeTime(LocalDateTime.now());
            menuRepo.save(newItem);
        }
    }


    // сохранение измененного пункта меню с проведением некоторых проверок и коррекций
    // - specialPrice
    // - active
    // - archived
    // - changeTime
    // - imageURL
    public MenuItem updateMenuItem(MenuItem updatedMenuItem) {
        updatedMenuItem.setArchived(false);
        MenuItem oldItem = menuRepo.findById(updatedMenuItem.getId()).orElseThrow();

        if (updatedMenuItem.getSpecialPrice() == null)
            updatedMenuItem.setSpecialPrice(oldItem.getSpecialPrice());

        // если меняется статус активен/не активен, то меняем время
        if (oldItem.getActive() != updatedMenuItem.getActive())
            updatedMenuItem.setStatusChangeTime(LocalDateTime.now());
        else
            updatedMenuItem.setStatusChangeTime(oldItem.getStatusChangeTime());

        if (oldItem.getImageURL() != null &&
                (updatedMenuItem.getImageURL() == null || updatedMenuItem.getImageURL().trim().isEmpty() ))
            updatedMenuItem.setImageURL(oldItem.getImageURL());
        else if (oldItem.getImageURL() == null
                && (updatedMenuItem.getImageURL() == null || updatedMenuItem.getImageURL().trim().isEmpty() )) {
            updatedMenuItem.setImageURL(FOOD_DEFAULT_PNG);
        }
        // в модели сохраняется только ID группы, поэтому, чтобы класс был полным, сохраняем группы заново
        FoodGroup foodGroup = groupRepo.findById(updatedMenuItem.getFoodGroup().getGroupId()).orElseThrow();
        updatedMenuItem.setFoodGroup(foodGroup);
        return menuRepo.save(updatedMenuItem);
    }


    public MenuItem updateMenuItemStatus(Long id, Boolean isActive) {
        MenuItem menuItem = menuRepo.findById(id).orElseThrow();
        menuItem.setActive(isActive);
        menuItem.setStatusChangeTime(LocalDateTime.now());
        System.out.println("ИЗМЕНЕНИЕ АКТИВНОСТИ " + menuItem + " | " + isActive );
        return menuRepo.save(menuItem);
    }


    // для сохранения целостности БД блюда из меню не удаляются, а архивируются.
    // если нет связи с другими таблицами, то есть - если пункт меню не встречается в заказах, он будет полностью удалён.
    // при удалении/архивировании также удаляется картинка из папки (если она не стандартная), чтобы не плодить лишние файлы.
    // при архивировании картинка заменяется на стандартную deleted.png.
    public void archiveMenuItem(Long id){
        MenuItem item = menuRepo.findById(id).orElseThrow();
        if (orderRepo.existsByMenuItem(item)) {
            item.setActive(false);
            item.setArchived(true);
            item.setStatusChangeTime(LocalDateTime.now());
            menuRepo.save(item);
        } else {
            if (!item.getImageURL().equals(FOOD_DEFAULT_PNG)) {
                int lastIndexOfSlash = item.getImageURL().lastIndexOf("/");
                String imagePath = IMAGE_UPLOAD_DIR + item.getImageURL().substring(lastIndexOfSlash + 1);
                try {
                    Files.deleteIfExists(Paths.get(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            menuRepo.deleteById(id);
        }

    }


    // сохраняем картинку в файл и возвращаем имя файла для дальнейшего сохранения в БД
    public String loadImage(MultipartFile imageFile) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.");
        int lastIndexOfDot = imageFile.getOriginalFilename().lastIndexOf(".");
        String fileType = (lastIndexOfDot == -1) ? "" : imageFile.getOriginalFilename().substring(lastIndexOfDot + 1);
        String fileName = dateFormat.format(new Date()) + fileType;
        String filePath = IMAGE_UPLOAD_DIR + fileName;
        Files.copy(imageFile.getInputStream(), Paths.get(filePath));
        return fileName;
    }

    public void restoreItemFromArchiveById(Long id) {
        MenuItem itemToRestore = menuRepo.findById(id).orElseThrow();
        itemToRestore.setArchived(false);
        menuRepo.save(itemToRestore);
    }
}
