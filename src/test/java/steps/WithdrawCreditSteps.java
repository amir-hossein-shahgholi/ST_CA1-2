package steps;

import static org.junit.Assert.*;

import model.User;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class WithdrawCreditSteps {

    private User user;
    private Exception thrownException;

    @Given("^a user with a credit balance of (\\d+)$")
    public void a_user_with_a_credit_balance_of(int credit) {
        user = new User();
        user.setCredit(credit);
    }

    @When("^the user tries to withdraw (-?\\d+)$")
    public void the_user_tries_to_withdraw(int amount) {
        try {
            user.withdrawCredit(amount);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("^the new credit balance should be (\\d+)$")
    public void the_new_credit_balance_should_be(int expectedBalance) {
        assertEquals(expectedBalance, user.getCredit(), 0);
    }

    @Then("^an InsufficientCredit exception should be thrown$")
    public void an_insufficient_credit_exception_should_be_thrown() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof InsufficientCredit);
    }

    @Then("^an InvalidCreditRange exception should be thrown$")
    public void an_invalid_credit_range_exception_should_be_thrown() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof InvalidCreditRange);
    }
}
