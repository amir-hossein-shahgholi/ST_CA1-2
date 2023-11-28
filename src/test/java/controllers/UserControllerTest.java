package controllers;
import application.BalootApplication;
import exceptions.InvalidCreditRange;
import exceptions.NotExistentUser;
import model.User;
import static defines.Errors.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import service.Baloot;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class})
@SpringBootTest(classes={BalootApplication.class})
public class UserControllerTest {

    private User user;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Baloot baloot;

    @Autowired
    private UserController userController;

    @Mock
    private User mockedUser;

    @BeforeEach
    public void setUp() {
        userController.setBaloot(baloot);
        user = new User("testUser", "testPass", "test@ut.ac.ir", "10/12/2023", "sample");
    }

    @Test
    public void testGetUserByIdSuccessfully() throws Exception {
        String userId = user.getUsername();
        when(baloot.getUserById(anyString())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(user.getPassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value(user.getBirthDate()));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        String nonExistentUserId = "nonExistentUser";
        when(baloot.getUserById(nonExistentUserId)).thenThrow(new NotExistentUser());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", nonExistentUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testAddCreditToUserSuccessfully() throws Exception {
        float creditToAdd = 500;
        when(baloot.getUserById(anyString())).thenReturn(mockedUser);
        doNothing().when(mockedUser).addCredit(anyInt());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{id}/credit", "sample")
                        .content("{\"credit\": " + creditToAdd + "}")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddCreditToUserWithInvalidCreditRange() throws Exception {
        float creditToAdd = -500;
        when(baloot.getUserById(anyString())).thenReturn(mockedUser);
        doThrow(new InvalidCreditRange()).when(mockedUser).addCredit(creditToAdd);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{id}/credit", "sample")
                        .content("{\"credit\": " + creditToAdd + "}")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(INVALID_CREDIT_RANGE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddCreditToUserWithInvalidCreditNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{id}/credit", "Sample")
                        .content("{\"credit\": \"invalidCredit\"}")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("Please enter a valid number for the credit amount."))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddCreditToUserWithInvalidUserId() throws Exception {
        float creditToAdd = 500;
        when(baloot.getUserById(anyString())).thenThrow(new NotExistentUser());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{id}/credit", "Sample")
                        .content("{\"credit\": " + creditToAdd + "}")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(NOT_EXISTENT_USER))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
