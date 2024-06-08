package ru.gb.cafeteria.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.BonusCard;
import ru.gb.cafeteria.exceptions.DuplicatePhoneException;
import ru.gb.cafeteria.services.BonusCardService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bonus-cards")
public class BonusCardController {

    private final BonusCardService bonusCardService;

    @Autowired
    public BonusCardController(BonusCardService bonusCardService) {
        this.bonusCardService = bonusCardService;
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneNumberExists(@RequestParam String phone) {
        boolean exists = bonusCardService.existsByClientPhone(phone);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity createBonusCard(@RequestBody BonusCard bonusCard) {
        try {
            bonusCardService.createBonusCard(bonusCard);
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (DuplicatePhoneException ex) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
}