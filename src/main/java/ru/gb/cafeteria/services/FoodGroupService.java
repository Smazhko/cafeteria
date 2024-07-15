package ru.gb.cafeteria.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.FoodGroup;
import ru.gb.cafeteria.exceptions.FoodGroupDeleteException;
import ru.gb.cafeteria.repository.FoodGroupRepository;
import ru.gb.cafeteria.repository.MenuItemRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FoodGroupService {
    private FoodGroupRepository foodGroupRepo;
    private MenuItemRepository menuItemRepo;

    @TrackUserAction
    public List<FoodGroup> getAllFoodGroups() {
        return foodGroupRepo.findAll().stream()
                .sorted(Comparator.comparingInt(FoodGroup::getPosition))
                .toList();
    }

    public FoodGroup getFoodGroupById(Long id) {
        return foodGroupRepo.findById(id).orElseThrow();
    }


    // обновляем группу.
    // сохраняем только, если имя уникальное
    @Transactional
    public void updateFoodGroup (FoodGroup updatedGroup){
        if (!foodGroupRepo.existsByName(updatedGroup.getName()))
            foodGroupRepo.save(updatedGroup);
    }

    // создаём новую группу,
    // проверяем, чтобы имя было непустое и не такое, которое уже существует
    @Transactional
    public void addNewFoodGroup (FoodGroup newGroup) {
        if (!newGroup.getName().isEmpty()
        && !newGroup.getName().isBlank()
        && !foodGroupRepo.existsByName(newGroup.getName())){
            newGroup.setPosition(getAllFoodGroups().size());
            foodGroupRepo.save(newGroup);
        }
        rePositionGroups();
    }

    // удаление группы - если только у ней нет блюд.
    // после удаления пересчитываем позиции групп по порядку.
    @Transactional
    public void deleteFoodGroupById (Long id){
        FoodGroup groupToDelete = getFoodGroupById(id);
        if (menuItemRepo.findByFoodGroup(groupToDelete).isEmpty()) {
            foodGroupRepo.deleteById(id);
            rePositionGroups();
        }
        else
            throw new FoodGroupDeleteException();
    }

    @TrackUserAction
    @Transactional
    // обновляем позиции групп в списке, чтоб не было пропущенных - начиная с нуля
    private void rePositionGroups() {
        List<FoodGroup> groupsList = foodGroupRepo.findAll().stream()
                .sorted(Comparator.comparingInt(FoodGroup::getPosition))
                .toList();
        for (int i = 0; i < groupsList.size(); i++) {
            FoodGroup currGroup = groupsList.get(i);
            currGroup.setPosition(i);
        }
        foodGroupRepo.saveAll(groupsList);
    }

    // меняем последовательность групп,
    // учитываем начало или конец списка
    @Transactional
    public void changeFoodGroupOrder(Long groupId, boolean moveUp) {
        FoodGroup currGroup = foodGroupRepo.findById(groupId).orElseThrow();
        Integer currPosition = currGroup.getPosition();
        FoodGroup swapGroup;
        if (moveUp && currPosition > 0) {
            swapGroup = foodGroupRepo.findByPosition(currPosition - 1);
            currGroup.setPosition(currPosition - 1);
            swapGroup.setPosition(currPosition);
            foodGroupRepo.save(currGroup);
            foodGroupRepo.save(swapGroup);
        } else if (!moveUp && currPosition < getAllFoodGroups().size() - 1) {
            swapGroup = foodGroupRepo.findByPosition(currPosition + 1);
            currGroup.setPosition(currPosition + 1);
            swapGroup.setPosition(currPosition);
            foodGroupRepo.save(currGroup);
            foodGroupRepo.save(swapGroup);
        } else {
            currGroup.setPosition(currPosition);
        }
        rePositionGroups();
    }

    // получаем количество блюд в каждой группе
    public Map<Long, Integer> getFoodCountsByGroup() {
        List<FoodGroup> groups = getAllFoodGroups();
        Map<Long, Integer> foodCounts = new HashMap<>();
        for (FoodGroup group : groups) {
            Integer count = menuItemRepo.countByFoodGroup(group);
            foodCounts.put(group.getGroupId(), count);
        }
        return foodCounts;
    }
}
