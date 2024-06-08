package ru.gb.cafeteria.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicatePhoneException extends DataIntegrityViolationException {
    public DuplicatePhoneException(String message) {
        super(message);
    }
}