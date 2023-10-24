package model;

import model.User;
import model.Commodity;
import exceptions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    private User user;
    private Commodity commodity;

    @BeforeEach
    public void setUp() {
        user = new User("testUser", "testPass", "test@ut.ac.ir", "10/12/2023", "sample");
        commodity = new Commodity();
        commodity.setId("testId");
        commodity.setName("testName");
        commodity.setPrice(10000);
        commodity.setInStock(10);
    }
    @AfterEach
    public void tearDown() {
        user = null;
        commodity = null;
    }
    @ParameterizedTest
    @ValueSource(floats = { 50.0f, 0.0f })
    public void testAddCreditSuccessfully(float credit) throws InvalidCreditRange {
        user.addCredit(credit);
        assertEquals(credit, user.getCredit());
    }

    @Test
    public void testAddCreditWithNegativeAmountShouldFail() {
        assertThrows(InvalidCreditRange.class, () -> user.addCredit(-10.0f));
    }

    @Test
    public void testAddCreditShouldSumWithLastCreditSuccessfully() throws InvalidCreditRange {
        user.setCredit(100.0f);
        user.addCredit(20);
        assertEquals(120.0f, user.getCredit());
    }

    @ParameterizedTest
    @ValueSource(floats = { 50.0f, 100.0f })
    public void testWithdrawCreditSuccessfully(float withdraw) throws InvalidCreditRange, InsufficientCredit {
        user.setCredit(100.0f);
        user.withdrawCredit(withdraw);
        assertEquals(100.0f - withdraw, user.getCredit());
    }

    @Test
    public void testWithdrawCreditWithInvalidAmountShouldFail() {
        user.setCredit(100.0f);
        assertThrows(InvalidCreditRange.class, () -> user.withdrawCredit(-50.0f));
    }

    @Test
    public void testWithdrawCreditWithInsufficientFundsShouldFail() {
        user.setCredit(20.0f);
        assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(50.0f));
    }

    @Test
    public void testAddBuyItemSuccessfully() {
        user.addBuyItem(commodity);
        assertEquals(user.getBuyList().get(commodity.getId()).intValue(), 1);
    }

    @Test
    public void testAddBuyItemToExistingItemSuccessfully() {
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        assertEquals(user.getBuyList().get(commodity.getId()).intValue(), 3);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 100 })
    public void testAddPurchasedItemSuccessfully(int quantity) throws InvalidQuantityRange {
        user.addPurchasedItem(commodity.getId(), quantity);
        assertEquals(user.getPurchasedList().get(commodity.getId()).intValue(), quantity);
    }

    @Test
    public void testAddPurchasedItemWithNegativeQuantityShouldFail() {
        assertThrows(InvalidQuantityRange.class, () -> user.addPurchasedItem(commodity.getId(), -100));
    }

    @Test
    public void testAddPurchasedItemToExistingPurchaseSuccessfully() throws InvalidQuantityRange {
        user.addPurchasedItem(commodity.getId(), 100);
        user.addPurchasedItem(commodity.getId(), 20);
        assertEquals(user.getPurchasedList().get(commodity.getId()).intValue(), 120);
    }
    @Test
    public void testRemoveOneItemFromBuyListSuccessfully() throws CommodityIsNotInBuyList {
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertEquals(user.getBuyList().get(commodity.getId()).intValue(), 1);
    }

    @Test
    public void testRemoveItemFromBuyListSuccessfully() throws CommodityIsNotInBuyList {
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertFalse(user.getBuyList().containsKey(commodity.getId()));
    }

    @Test
    public void testRemoveItemFromBuyListWithNotExistingItemSuccessfully() {
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity));
    }
}