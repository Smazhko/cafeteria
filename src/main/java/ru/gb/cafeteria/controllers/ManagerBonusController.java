package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.domain.DiscountService;
import ru.gb.cafeteria.domain.DiscountType;
import ru.gb.cafeteria.services.BonusCardService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/manager/bonus")
public class ManagerBonusController {

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
        model.addAttribute("isDeletePossible", false);
        return "manager/discount-form";
    }


    @PostMapping("/save")
    public String saveDiscount(@ModelAttribute DiscountType discount) {
        discountService.saveDiscount(discount);
        return "redirect:/manager/bonus";
    }

    @GetMapping("/edit/{id}")
    public String showEditDiscountForm(@PathVariable Long id, Model model) {
        DiscountType discount = discountService.getDiscountById(id);
        model.addAttribute("discount", discount);
        model.addAttribute("isDeletePossible", discountService.isDeleteByIdPossible(id));
        return "/manager/discount-form";
    }

    @TrackUserAction
    @PostMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {
        System.out.println("DELETING " + id);
        discountService.deleteDiscountById(id);
        return "redirect:/manager/bonus";
    }

    @TrackUserAction
    @PostMapping("/delete")
    public String deleteDiscount2(@RequestParam Long id) {
        System.out.println("DELETING 2" + id);
        discountService.deleteDiscountById(id);
        return "redirect:/manager/bonus";
    }

    @PostMapping("/inactivate")
    public String inactivateDiscount(@RequestParam Long id) {
        discountService.changeActivityById(id, false);
        return "redirect:/manager/bonus";
    }

    @PostMapping("/activate")
    public String activateDiscount(@RequestParam Long id) {
        discountService.changeActivityById(id, true);
        return "redirect:/manager/bonus";
    }
}
