package controllers;

import controllers.AuthenticationController;
import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

        doThrow(NotExistentUser.class).when(baloot).login(anyString(), anyString());

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        //assertEquals("User does not exist.", response.getBody());

    }
    @Test
    public void testLoginIncorrectPassword() throws NotExistentUser, IncorrectPassword {
        String username = "testUser";
        String password = "testpassword";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        doThrow(IncorrectPassword.class).when(baloot).login(anyString(), anyString());

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        //assertEquals("Incorrect password.", response.getBody());
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
        assertEquals("The username is already taken.", response.getBody());
    }
}

