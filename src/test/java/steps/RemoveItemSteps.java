package steps;

import static org.junit.Assert.*;

import model.Commodity;
import model.User;
import exceptions.CommodityIsNotInBuyList;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RemoveItemSteps {

    private User user;
    private Exception thrownException;

    @Given("^a user with a commodity \"([^\"]*)\" in their buy list with quantity (\\d+)$")
    public void a_user_with_commodity_in_their_buy_list(String commodityId, int quantity) {
        user = new User();
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        commodity.setName("sampleName");
        commodity.setProviderId("1");
        commodity.setPrice(100);
        commodity.getCategories().add("cat-1");
        commodity.setRating(0.0f);
        commodity.setInStock(10);
        commodity.setImage("sampleImage");
        commodity.setInitRate(5.0f);
        user.getBuyList().put(commodity.getId(), quantity);
    }

    @Given("^a user without commodity \"([^\"]*)\" in their buy list$")
    public void a_user_without_commodity_in_their_buy_list(String commodityId) {
        user = new User();
    }

    @When("^the user tries to remove commodity \"([^\"]*)\" from their buy list$")
    public void the_user_tries_to_remove_commodity_from_their_buy_list(String commodityId) {
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        try {
            user.removeItemFromBuyList(commodity);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("^the quantity of commodity \"([^\"]*)\" in the buy list should be (\\d+)$")
    public void the_quantity_of_commodity_in_the_buy_list_should_be(String commodityId, int expectedQuantity) {
        assertEquals(expectedQuantity, (int) user.getBuyList().get(commodityId));
    }

    @Then("^the buy list should not contain commodity \"([^\"]*)\"$")
    public void the_buy_list_should_not_contain_commodity(String commodityId) {
        assertFalse(user.getBuyList().containsKey(commodityId));
    }

    @Then("^a CommodityIsNotInBuyList exception should be thrown$")
    public void a_commodity_is_not_in_buy_list_exception_should_be_thrown() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof CommodityIsNotInBuyList);
    }
}
