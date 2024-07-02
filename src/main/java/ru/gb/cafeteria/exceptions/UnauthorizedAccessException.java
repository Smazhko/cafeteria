package ru.gb.cafeteria.exceptions;

public class UnauthorizedAccessException  extends IllegalAccessException {
    public UnauthorizedAccessException (String actionType) {
        super("< <<  !!!  >> > Нет доступа к действию:  \"" + actionType + "\". Обратитесь к администратору.");
    }
}