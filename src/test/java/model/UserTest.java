package model;

import model.User;
import model.Commodity;
import exceptions.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class UserTest {
    private User user;
    private Commodity commodity;

    @Before
    public void setUp() {
        user = new User("testUser", "testPass", "test@ut.ac.ir", "10/12/2023", "sample");
        commodity = new Commodity();
        commodity.setId("testId");
        commodity.setName("testName");
        commodity.setPrice(10000);
        commodity.setInStock(10);
    }

    @Test
    public void testAddCreditSuccessfully() {
        try {
            user.addCredit(50.0f);
            assertEquals(50.0f, user.getCredit(), 0);
        } catch (InvalidCreditRange e) {
            fail("InvalidCreditRange exception should not be thrown for positive credit.");
        }
    }

    @Test
    public void testAddCreditWithNegativeAmountShouldFail() {
        try {
            user.addCredit(-10.0f);
            fail("InvalidCreditRange exception should be thrown for negative credit.");
        } catch (InvalidCreditRange e) {
        }
    }

    @Test
    public void testAddCreditShouldSumWithLastCreditSuccessfully() {
        user.setCredit(100.0f);
        try {
            user.addCredit(50.0f);
            assertEquals(150.0f, user.getCredit(), 0);
        } catch (InvalidCreditRange e) {
            fail("InvalidCreditRange exception should not be thrown for positive credit.");
        }
    }

    @Test
    public void testWithdrawCreditSuccessfully() {
        user.setCredit(100.0f);
        try {
            user.withdrawCredit(50.0f);
            assertEquals(50.0f, user.getCredit(), 0);
        } catch (InsufficientCredit | InvalidCreditRange e) {
            fail("InsufficientCredit exception should not be thrown for sufficient credit.");
        }
    }

    @Test
    public void testWithdrawCreditWithInvalidAmountShouldFail() {
        user.setCredit(100.0f);
        try {
            user.withdrawCredit(-50.0f);
            fail("InvalidCreditRange exception should be thrown for negative credit.");
        } catch (InvalidCreditRange e) {
        } catch (InsufficientCredit e){
            fail("InvalidCreditRange exception should be thrown for negative credit.");
        }
    }

    @Test
    public void testWithdrawCreditWithInsufficientFundsShouldFail() {
        user.setCredit(20.0f);
        try {
            user.withdrawCredit(50.0f);
            fail("InsufficientCredit exception should be thrown for insufficient credit.");
        } catch (InsufficientCredit e) {
        } catch (InvalidCreditRange e) {
            fail("InsufficientCredit exception should be thrown for insufficient credit.");
        }
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

    @Test
    public void testAddPurchasedItemSuccessfully() {
        try {
            user.addPurchasedItem(commodity.getId(), 100);
            assertEquals(user.getPurchasedList().get(commodity.getId()).intValue(), 100);
        }catch (InvalidQuantityRange e)
        {
            fail("InvalidQuantityRange exception should not be thrown for positive quantity.");
        }
    }

    @Test
    public void testAddPurchasedItemWithNegativeQuantityShouldFail() {
        try {
            user.addPurchasedItem(commodity.getId(), -100);
            fail("InvalidQuantityRange exception should be thrown for negative quantity.");
        } catch (InvalidQuantityRange e) {
        }
    }

    @Test
    public void testAddPurchasedItemToExistingPurchaseSuccessfully() {
        try {
            user.addPurchasedItem(commodity.getId(), 100);
            user.addPurchasedItem(commodity.getId(), 20);
            assertEquals(user.getPurchasedList().get(commodity.getId()).intValue(), 120);
        }catch (InvalidQuantityRange e)
        {
            fail("InvalidQuantityRange exception should not be thrown for positive quantity.");
        }
    }
    @Test
    public void testRemoveOneItemFromBuyListSuccessfully() {
        try {
            user.addBuyItem(commodity);
            user.addBuyItem(commodity);
            user.removeItemFromBuyList(commodity);
            assertEquals(user.getBuyList().get(commodity.getId()).intValue(), 1);
        } catch (CommodityIsNotInBuyList e){
            fail("CommodityIsNotInBuyList exception should not be thrown.");
        }
    }

    @Test
    public void testRemoveItemFromBuyListSuccessfully() {
        try {
            user.addBuyItem(commodity);
            user.removeItemFromBuyList(commodity);
            assertFalse(user.getBuyList().containsKey(commodity.getId()));
        } catch (CommodityIsNotInBuyList e){
            fail("CommodityIsNotInBuyList exception should not be thrown.");
        }
    }

    @Test
    public void testRemoveItemFromBuyListWithNotExistingItemSuccessfully() {
        try {
            user.removeItemFromBuyList(commodity);
            fail("CommodityIsNotInBuyList exception should be thrown.");
        } catch (CommodityIsNotInBuyList e){
        }
    }
}