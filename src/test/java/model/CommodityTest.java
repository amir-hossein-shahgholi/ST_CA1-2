package model;

import exceptions.InvalidRateRange;
import exceptions.NotInStock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {
    private Commodity commodity;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
        commodity.setId("testId");
        commodity.setName("testName");
        commodity.setPrice(10000);
    }
    @AfterEach
    public void tearDown() {
        commodity = null;
    }
    @ParameterizedTest
    @ValueSource(ints = { 50, -10, 0, -5  })
    public void testUpdateInStockSuccessfully(int new_stock) throws NotInStock {
        commodity.setInStock(10);
        commodity.updateInStock(new_stock);
        assertEquals(10 + new_stock, commodity.getInStock());
    }

    @Test
    public void testUpdateInStockWithInvalidAmountShouldFail() {
        commodity.setInStock(10);
        assertThrows(NotInStock.class, () -> commodity.updateInStock(-11));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 10, 4 })
    public void testAddRateForAnUserSuccessfully(int rating) throws InvalidRateRange{
        commodity.addRate("testUser1", rating);
        assertEquals((float) rating / 2, commodity.getRating());
        assertTrue(commodity.getUserRate().containsKey("testUser1"));
    }

    @Test
    public void testAddRateWithExistingKeyShouldUpdateUserRateSuccessfully() throws InvalidRateRange{
        commodity.addRate("testUser1", 4);
        commodity.addRate("testUser1", 6);
        assertEquals(3, commodity.getRating());
        assertTrue(commodity.getUserRate().containsKey("testUser1"));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 11 })
    public void testInvalidRateRangeShouldFail(int rating) {
        assertThrows(InvalidRateRange.class, () -> commodity.addRate("testUser1", rating));
    }
    @Test
    public void testAddMultipleUsersSuccessfully() throws InvalidRateRange{
        commodity.addRate("testUser1", 1);
        commodity.addRate("testUser2", 2);
        commodity.addRate("testUser3", 3);
        assertEquals(1.5, commodity.getRating());
    }
}
