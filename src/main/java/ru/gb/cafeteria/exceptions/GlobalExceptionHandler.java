package ru.gb.cafeteria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gb.cafeteria.exceptions.UnauthorizedAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccessException(UnauthorizedAccessException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "/error/error-exception"; // Имя HTML страницы для отображения ошибки
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicatePhoneException(DuplicatePhoneException ex) {
        return ex.getMessage();
    }

}