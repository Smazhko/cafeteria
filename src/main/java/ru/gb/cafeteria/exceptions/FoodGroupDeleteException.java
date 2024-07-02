package ru.gb.cafeteria.exceptions;

public class FoodGroupDeleteException extends RuntimeException{
    public FoodGroupDeleteException() {
        super("Нельзя удалить группу, ассоциированную с какими-либо пунктами меню.\n" +
                "Для удаления группы необходимо перенести пункты меню из группы, подлежащей удалению, в другие группы.");
    }
}
