package ru.gb.cafeteria.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UsernameNotFoundException extends EntityNotFoundException {
    public UsernameNotFoundException(String userName) {
        super("< <<  !!!  >> > User with name \"" + userName + "\" not found.");

    }
}