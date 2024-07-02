package ru.gb.cafeteria.bonusSystem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;
import ru.gb.cafeteria.exceptions.DuplicatePhoneException;
import ru.gb.cafeteria.bonusSystem.services.BonusCardService;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/bonus-cards")
public class BonusCardController {

    private BonusCardService bonusCardService;

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