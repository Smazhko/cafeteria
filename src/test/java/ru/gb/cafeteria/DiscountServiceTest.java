package ru.gb.cafeteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gb.cafeteria.domain.DiscountType;
import ru.gb.cafeteria.repository.BonusCardRepository;
import ru.gb.cafeteria.repository.DiscountRepository;
import ru.gb.cafeteria.services.DiscountService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepo;

    @Mock
    private BonusCardRepository cardRepo;

    @InjectMocks
    private DiscountService discountService;

    private DiscountType discount;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        discount = new DiscountType();
        discount.setDiscountId(1L);
        discount.setDiscountName("Test Discount");
        discount.setDescription("Test Description");
        discount.setDiscountPercent(10);
        discount.setMinSum(BigDecimal.ZERO);
        discount.setMaxSum(BigDecimal.valueOf(1000));
        discount.setActive(true);
    }

    @Test
    public void testGetAllDiscounts() {
        when(discountRepo.findAll()).thenReturn(Arrays.asList(discount));
        List<DiscountType> discounts = discountService.getAllDiscounts();
        assertEquals(1, discounts.size());
        assertEquals(discount, discounts.get(0));
    }

    @Test
    public void testGetDiscountById() {
        when(discountRepo.findById(1L)).thenReturn(Optional.of(discount));
        DiscountType foundDiscount = discountService.getDiscountById(1L);
        assertEquals(discount, foundDiscount);
    }

    @Test
    public void testSaveDiscount_Valid() {
        discountService.saveDiscount(discount);
        verify(discountRepo, times(1)).save(discount);
    }

    @Test
    public void testSaveDiscount_InvalidPercent() {
        discount.setDiscountPercent(150);
        discountService.saveDiscount(discount);
        assertEquals(0, discount.getDiscountPercent());
        verify(discountRepo, times(1)).save(discount);
    }

    @Test
    public void testSaveDiscount_MaxSumLessThanMinSum() {
        discount.setMaxSum(BigDecimal.ZERO);
        discount.setMinSum(BigDecimal.TEN);
        discountService.saveDiscount(discount);
        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), discount.getMaxSum());
        verify(discountRepo, times(1)).save(discount);
    }

    @Test
    public void testDeleteDiscountById_DeletionPossible() {
        when(cardRepo.countByDiscountType_DiscountId(1L)).thenReturn(0L);
        discountService.deleteDiscountById(1L);
        verify(discountRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteDiscountById_DeletionNotPossible() {
        when(cardRepo.countByDiscountType_DiscountId(1L)).thenReturn(1L);
        discountService.deleteDiscountById(1L);
        verify(discountRepo, times(0)).deleteById(1L);
    }

    @Test
    public void testIsDeleteByIdPossible_True() {
        when(cardRepo.countByDiscountType_DiscountId(1L)).thenReturn(0L);
        assertTrue(discountService.isDeleteByIdPossible(1L));
    }

    @Test
    public void testIsDeleteByIdPossible_False() {
        when(cardRepo.countByDiscountType_DiscountId(1L)).thenReturn(1L);
        assertFalse(discountService.isDeleteByIdPossible(1L));
    }

    @Test
    public void testChangeActivityById() {
        when(discountRepo.findById(1L)).thenReturn(Optional.of(discount));
        discountService.changeActivityById(1L, false);
        assertFalse(discount.getActive());
        verify(discountRepo, times(1)).save(discount);
    }
}