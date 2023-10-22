package model;

import exceptions.InvalidRateRange;
import exceptions.NotInStock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommodityTest {
    private Commodity commodity;

    @Before
    public void setUp() {
        commodity = new Commodity();
        commodity.setId("testId");
        commodity.setName("testName");
        commodity.setPrice(10000);
        commodity.setInStock(10);
    }

    @Test
    public void testUpdateInStockSuccessfully() {
        try {
            int last_stock = commodity.getInStock();
            commodity.updateInStock(50);
            assertEquals(last_stock + 50, commodity.getInStock());
        } catch (NotInStock e) {
            fail("NotInStock exception should not be thrown for a positive stock update.");
        }
    }

    @Test
    public void testUpdateInStockToZeroSuccessfully() {
        try {
            int last_stock = commodity.getInStock();
            commodity.updateInStock(last_stock*-1);
            assertEquals(0, commodity.getInStock());
        } catch (NotInStock e) {
            fail("NotInStock exception should not be thrown for a positive stock update.");
        }
    }

    @Test
    public void testUpdateInStockWithInvalidAmountShouldFail() {
        try {
            commodity.updateInStock(-100);
            fail("NotInStock exception should be thrown for a negative stock update.");
        } catch (NotInStock e) {

        }
    }

    @Test
    public void testAddRateForAnUserSuccessfully() {
        try {
            commodity.addRate("testUser1", 4);
            assertEquals(2, commodity.getRating(), 0);
            assertTrue(commodity.getUserRate().containsKey("testUser1"));
        }catch (InvalidRateRange e){
            fail("InvalidRateRange exception should not be thrown for a positive rate add.");
        }
    }

    @Test
    public void testAddRateWithExistingKeyShouldUpdateUserRateSuccessfully() {
        try {
            commodity.addRate("testUser1", 4);
            commodity.addRate("testUser1", 6);
            assertEquals(3, commodity.getRating(), 0);
            assertTrue(commodity.getUserRate().containsKey("testUser1"));
        }catch (InvalidRateRange e) {
            fail("InvalidRateRange exception should not be thrown for a positive rate add.");
        }
    }

    @Test
    public void testAddNegativeRateShouldFail() {
        try{
            commodity.addRate("testUser1", -4);
        fail("InvalidRateRange exception should be thrown for negative credit.");
        } catch (InvalidRateRange e) {
        }
    }

    @Test
    public void testCalcRatingShouldCalcRateSuccessfully() {
        try {
            commodity.addRate("testUser1", 1);
            commodity.addRate("testUser2", 2);
            commodity.addRate("testUser3", 3);
            assertEquals(1.5, commodity.getRating(), 0);
        }catch (InvalidRateRange e) {
            fail("InvalidRateRange exception should not be thrown for a positive rate add.");
        }
    }
}
