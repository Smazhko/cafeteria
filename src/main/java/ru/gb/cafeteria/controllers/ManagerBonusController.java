package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.domain.DiscountService;
import ru.gb.cafeteria.domain.DiscountType;
import ru.gb.cafeteria.services.BonusCardService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/manager/bonus")
public class ManagerBonusController {

    private final BonusCardService bonusCardService;
    private final DiscountService discountService;

    @GetMapping
    public String showDiscounts(Model model) {
        List<DiscountType> discounts = discountService.getAllDiscounts();
        model.addAttribute("discounts", discounts);
        return "manager/discounts";
    }

    @GetMapping("/add")
    public String showAddDiscountForm(Model model) {
        model.addAttribute("discount", new DiscountType());
        return "manager/discount-form";
    }

    @PostMapping("/add")
    public String addDiscount(@ModelAttribute DiscountType discount) {
        discountService.saveDiscount(discount);
        return "redirect:/manager/bonus";
    }

    @GetMapping("/edit/{id}")
    public String showEditDiscountForm(@PathVariable Long id, Model model) {
        DiscountType discount = discountService.getDiscountById(id);
        model.addAttribute("discount", discount);
        return "manager/discount-form";
    }

    @PostMapping("/edit")
    public String editDiscount(@ModelAttribute DiscountType discount) {
        discountService.saveDiscount(discount);
        return "redirect:/manager/bonus";
    }

    @PostMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscountById(id);
        return "redirect:/manager/bonus";
    }
}

