package ru.gb.cafeteria.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.gb.cafeteria.aspects.TrackUserAction;
import ru.gb.cafeteria.bonusSystem.domain.BonusCard;
import ru.gb.cafeteria.bonusSystem.repository.BonusCardRepository;
import ru.gb.cafeteria.domain.*;
import ru.gb.cafeteria.dto.BasketDTO;
import ru.gb.cafeteria.dto.BasketItemDTO;
import ru.gb.cafeteria.exceptions.UnauthorizedAccessException;
import ru.gb.cafeteria.repository.*;
import ru.gb.cafeteria.security.domain.User;
import ru.gb.cafeteria.security.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReceiptService {

    private ReceiptRepository receiptRepo;
    private ReceiptStatusRepository rStatusRepo;
    private OrderRepository orderRepo;
    private OrderStatusRepository orderStatusRepo;
    private BonusCardRepository cardRepo;
    private StaffRepository staffRepo;
    private UserRepository userRepo;


    public ReceiptStatus getReceiptStatusByName(String name) {
        return rStatusRepo.findByStatusName(name);
    }


    public List<ReceiptStatus> getAllReceiptStatuses() {
        return rStatusRepo.findAll();
    }


    public List<Receipt> getFilteredReceipts(LocalDate startDate, LocalDate endDate, Long statusId) {
        if (startDate == null) {
            return (statusId == null || statusId == 0) ?
                    receiptRepo.findAllByOpenTimeBefore(endDate.atStartOfDay().plusDays(1)) :
                    receiptRepo.findAllByOpenTimeBeforeAndReceiptStatus(endDate.atStartOfDay().plusDays(1), rStatusRepo.findById(statusId).orElseThrow());
        } else {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atStartOfDay().plusDays(1);
            return (statusId == null || statusId == 0) ?
                    receiptRepo.findAllByOpenTimeBetween(start, end) :
                    receiptRepo.findAllByOpenTimeBetweenAndReceiptStatus(start, end, rStatusRepo.findById(statusId).orElseThrow());
        }
    }


    public List<Receipt> getAllReceipts() {
        return receiptRepo.findAll();
    }


    public Receipt getReceiptById(Long receiptId) {
        return receiptRepo.findById(receiptId).orElseThrow(() -> new RuntimeException("Receipt not found"));
    }


    // формирование чека на основании корзины
    // получить сотрудника, который составляет чек
    // СФОРМИРОВАТЬ ЧЕК:
    // 1. создать новый пустой чек
    // 2. сформировать список заказов на основании корзины
    // 3. сохранить список заказов в чек
    // 4. сохранить чек
    @Transactional
    @TrackUserAction
    public Receipt createNewReceipt(BasketDTO basket) {
        // Получение текущего пользователя
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("Unable to get current user");
        }
        Staff staff = staffRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Receipt newReceipt = new Receipt();
        newReceipt.setOpenTime(LocalDateTime.now());
        newReceipt.setReceiptStatus(getReceiptStatusByName("OPEN"));
        newReceipt.setStaff(staff);
        receiptRepo.save(newReceipt);
        List<Order> orderList = createOrderList(newReceipt, basket);
        newReceipt.setReceiptStatus(getReceiptStatusByName("READY TO PAY"));
        newReceipt.setOrderList(orderList);
        saveReceipt(newReceipt);
        return newReceipt;
    }


    @TrackUserAction
    public List<Order> createOrderList(Receipt receipt, BasketDTO basket) {
        OrderStatus formedStatus = orderStatusRepo.findByStatusName("FORMING");
        List<Order> orderList = new ArrayList<>();

        for (BasketItemDTO basketItem : basket.getItems()) {
            Order order = new Order();
            BigDecimal price =
                    basketItem.getMenuItem().getPrice().compareTo(basketItem.getMenuItem().getSpecialPrice()) > 0 ?
                            basketItem.getMenuItem().getSpecialPrice() : basketItem.getMenuItem().getPrice();
            int quantity = basketItem.getQuantity();
            BigDecimal sum = price.multiply(BigDecimal.valueOf(quantity));
            order.setMenuItem(basketItem.getMenuItem());
            order.setQuantity(quantity);
            order.setPrice(price);
            order.setSum(sum);
            order.setOrderStatus(formedStatus);
            order.setReceipt(receipt);
            orderList.add(order);
        }
        // orderRepo.saveAll(orderList);
        return orderList;
    }


    @Transactional
    @TrackUserAction
    public Receipt saveReceipt(Receipt receipt) {
        return receiptRepo.save(recalculateReceipt(receipt));
    }


    public Receipt applyBonusCard(Long receiptId, Long cardId) {
        Receipt receipt = getReceiptById(receiptId);
        BonusCard bonusCard = cardRepo.findById(cardId).orElseThrow();
        receipt.setBonusCard(bonusCard);
        recalculateReceipt(receipt);
        return saveReceipt(receipt);
    }


    @TrackUserAction
    public void payReceiptById(Long receiptId) {
        Receipt receipt = getReceiptById(receiptId);
        receipt.setReceiptStatus(getReceiptStatusByName("PAID"));
        receipt.setCloseTime(LocalDateTime.now());
        sendReceiptOrdersToKitchen(receipt);
        // добавить сумму чека в общую сумму на карте, если она была добавлена к чеку
        BonusCard card = receipt.getBonusCard();
        if (card != null) {
            card.setTotalSum(card.getTotalSum().add(receipt.getFinalSum()));
        }
        receiptRepo.save(receipt);
    }


    // для кассира - отмена неудавшегося чека по ID
    // если чек имеет статус OPEN или READY TO PAY, то он полностью удаляется.
    // оплаченные чеки кассиру удалить нельзя!
    @TrackUserAction
    public void cancelReceipt(Long receiptId) throws UnauthorizedAccessException {
        Receipt receipt = getReceiptById(receiptId);
        ReceiptStatus formingStatus = getReceiptStatusByName("OPEN");
        ReceiptStatus readyToPayStatus = getReceiptStatusByName("READY TO PAY");
        System.out.println("DELETING ... " + receipt.getReceiptStatus());
        if (receipt.getReceiptStatus().equals(formingStatus) ||
                receipt.getReceiptStatus().equals(readyToPayStatus)) {
            receiptRepo.deleteById(receiptId); // каскадное удаление заказов вслед за удалением чека
        } else {
            throw new UnauthorizedAccessException("Удаление чека со статусом \"PAID\" запрещено. ");
        }
        System.out.println("DELETING ... " + receipt.getReceiptId());
    }


    // удаление чеков для менеджера - полное каскадное удаление вместе с входящими в чеки заказами
    @TrackUserAction
    public void deleteReceiptById(Long id) {
        receiptRepo.deleteById(id);
    }


    // если список заказов в чеке не пуст - пересчёт общей суммы чека,
    // если есть бонусная карта - подсчёт суммы скидки
    // учитывается только процент скидки с бонусной карты
    // в конце - расчёт итоговой суммы чека
    // TODO - начисление и снятие бонусов
    @TrackUserAction
    private Receipt recalculateReceipt(Receipt receipt) {
        if (!receipt.getOrderList().isEmpty()) {
            receipt.setTotalSum(receipt.getOrderList().stream()
                    .map(order -> order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            if (receipt.getBonusCard() != null) {
                BonusCard card = receipt.getBonusCard();
                BigDecimal discountPercent = BigDecimal.valueOf(card.getDiscountType().getDiscountPercent()).divide(BigDecimal.valueOf(100));
                receipt.setDiscountSum(receipt.getTotalSum().multiply(discountPercent).setScale(2, RoundingMode.HALF_UP));
            } else {
                receipt.setDiscountSum(BigDecimal.ZERO);
            }
        } else {
            receipt.setTotalSum(BigDecimal.ZERO);
            receipt.setDiscountSum(BigDecimal.ZERO);
        }
        receipt.setFinalSum(receipt.getTotalSum().subtract(receipt.getDiscountSum()));
        return receipt;
    }

    @TrackUserAction
    public void sendReceiptOrdersToKitchen(Receipt receipt) {
        List<Order> orderList = receipt.getOrderList();
        for (Order order : orderList) {
            order.setOrderStatus(orderStatusRepo.findByStatusName("IN PROGRESS"));
        }
        saveReceipt(receipt);
    }
}
