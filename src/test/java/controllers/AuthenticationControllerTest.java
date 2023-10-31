package controllers;

import controllers.AuthenticationController;
import defines.Errors;
import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static defines.Errors.NOT_EXISTENT_USER;
import static defines.Errors.USERNAME_ALREADY_TAKEN;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationControllerTest {

    @Mock
    Baloot baloot;

    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController();
        authenticationController.setBaloot(baloot);
    }

    @Test
    public void testLoginSuccess() throws NotExistentUser, IncorrectPassword {
        String username = "testuser";
        String password = "testpassword";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        doNothing().when(baloot).login(anyString(), anyString());

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("login successfully!", response.getBody());

    }
    @Test
    public void testLoginNotExistentUser() throws NotExistentUser, IncorrectPassword {
        String username = "testuser";
        String password = "testpassword";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        doThrow(new NotExistentUser()).when(baloot).login(anyString(), anyString());

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_USER, response.getBody());

    }
    @Test
    public void testLoginIncorrectPassword() throws NotExistentUser, IncorrectPassword {
        String username = "testUser";
        String password = "testpassword";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        doThrow(new IncorrectPassword()).when(baloot).login(anyString(), anyString());

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(Errors.INCORRECT_PASSWORD, response.getBody());
        }
    @Test
    public void testSignupSuccess() throws UsernameAlreadyTaken {
        Map<String, String> input = new HashMap<>();
        input.put("address", "sample Address");
        input.put("birthDate", "1990-01-01");
        input.put("email", "test@ut.ac.it");
        input.put("username", "testuser");
        input.put("password", "password");

        doNothing().when(baloot).addUser(any(User.class));

        ResponseEntity<String> response = authenticationController.signup(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("signup successfully!", response.getBody());
    }
    @Test
    public void testSignupUsernameAlreadyTaken() throws UsernameAlreadyTaken {
        Map<String, String> input = new HashMap<>();
        input.put("address", "sample Address");
        input.put("birthDate", "1990-01-01");
        input.put("email", "test@ut.ac.it");
        input.put("username", "testuser");
        input.put("password", "password");

        doThrow(new UsernameAlreadyTaken()).when(baloot).addUser(any(User.class));

        ResponseEntity<String> response = authenticationController.signup(input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Errors.USERNAME_ALREADY_TAKEN, response.getBody());
    }
}

