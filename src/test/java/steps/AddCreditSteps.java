package steps;

import static org.junit.Assert.*;

import model.User;
import exceptions.InvalidCreditRange;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AddCreditSteps {

    private User user;
    private Exception thrownException;

    @Given("^A user with a credit balance of (\\d+)$")
    public void a_user_with_a_credit_balance_of(int credit) {
        user = new User();
        user.setCredit(credit);
    }

    @When("^the user tries to add (-?\\d+) credit$")
    public void the_user_tries_to_add_credit(int amount) {
        try {
            user.addCredit(amount);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("^The new credit balance should be (\\d+)$")
    public void the_new_credit_balance_should(int expectedBalance) {
        assertEquals(expectedBalance, user.getCredit(), 0);
    }

    @Then("^An InvalidCreditRange exception should be thrown$")
    public void an_invalid_credit_range_exception_should_thrown() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof InvalidCreditRange);
    }
}
