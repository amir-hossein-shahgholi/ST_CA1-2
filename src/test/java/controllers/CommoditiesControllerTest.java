package controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import defines.Errors;
import exceptions.InvalidRateRange;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
public class CommoditiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Baloot baloot;

    @Autowired
    private CommoditiesController commoditiesController;

    @Mock
    private User mockedUser;

    private
    Commodity commodity1;
    Commodity commodity2;
    Comment comment1;
    Comment comment2;
    User user;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        commoditiesController.setBaloot(baloot);
        commodity1 = new Commodity();
        commodity1.setId("1");
        commodity1.setName("testName");
        commodity1.setPrice(10000);
        commodity2 = new Commodity();
        commodity2.setId("2");
        commodity2.setName("testName2");
        commodity2.setPrice(20000);
        user = new User("testUser",
                "testPass",
                "test@ut.ac.ir",
                "10/12/2023",
                "sample");
        comment1 = new Comment(
                1,
                "sample@ut.ac.ir",
                "testUser",
                1,
                "Sample comment");
        comment2 = new Comment(
                2,
                "sample2@ut.ac.ir",
                "testUser2",
                1,
                "Sample comment2");
    }

    @Test
    public void testGetCommodities() {
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);
        when(baloot.getCommodities()).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testGetCommoditiesWithNoCommodities() {
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(baloot.getCommodities()).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testGetCommodity() throws NotExistentCommodity {
        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);

        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodity1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodity1, response.getBody());
    }

    @Test
    public void testGetCommodityNotExistentCommodity() throws NotExistentCommodity {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());

        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodity1.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRateCommoditySuccess() throws NotExistentCommodity, InvalidRateRange {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "5");

        Commodity commodity = mock(Commodity.class);

        when(baloot.getCommodityById(anyString())).thenReturn(commodity);

        doNothing().when(commodity).addRate(anyString(), anyInt());

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodity1.getId(), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rate added successfully!", response.getBody());

        verify(commodity).addRate(username, 5);
    }

    @Test
    public void testRateCommodityNotExistentCommodity() throws NotExistentCommodity {
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");
        input.put("rate", "5");

        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodity1.getId(), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_COMMODITY, response.getBody());
    }

    @Test
    public void testRateCommodityInvalidRateRange() throws NotExistentCommodity, InvalidRateRange {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "10");

        Commodity commodity = mock(Commodity.class);

        when(baloot.getCommodityById(anyString())).thenReturn(commodity);
        doThrow(new InvalidRateRange()).when(commodity).addRate(anyString(), anyInt());

        ResponseEntity<String> response = commoditiesController.rateCommodity("sample", input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Errors.INVALID_RATE_RANGE, response.getBody());
    }

    @Test
    public void testRateCommodityInvalidRatingInput() {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "invalidRating");

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodity1.getId(), input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("For input string: \"invalidRating\"", response.getBody());
    }

    @Test
    public void testAddCommodityCommentSuccess() throws NotExistentUser {
        String username = "testUser";
        String commentText = "Sample comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        int commentId = 1;
        when(baloot.generateCommentId()).thenReturn(commentId);
        when(baloot.getUserById(username)).thenReturn(user);
        doNothing().when(baloot).addComment(commentCaptor.capture());

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodity1.getId(), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("comment added successfully!", response.getBody());

        Comment capturedComment = commentCaptor.getValue();
        assertEquals(user.getEmail(), capturedComment.getUserEmail());
        assertEquals(user.getUsername(), capturedComment.getUsername());
        assertEquals(Integer.parseInt(commodity1.getId()), capturedComment.getCommodityId());
        assertEquals(commentText, capturedComment.getText());
    }

    @Test
    public void testAddCommodityCommentUserNotExistent() throws NotExistentUser {
        String username = "nonExistentUser";
        String commentText = "This is a comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);

        when(baloot.generateCommentId()).thenReturn(1);
        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodity1.getId(), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    public void testAddCommodityCommentInvalidCommodityId() throws NotExistentUser {
        String username = "nonExistentUser";
        String commentText = "This is a comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);

        when(baloot.generateCommentId()).thenReturn(1);
        when(baloot.getUserById(username)).thenReturn(user);

        ResponseEntity<String> response = commoditiesController.addCommodityComment("InvalidId", input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetCommodityCommentSuccess() {
        ArrayList<Comment> expectedComments = new ArrayList<>();
        expectedComments.add(comment1);
        expectedComments.add(comment2);

        when(baloot.getCommentsForCommodity(anyInt())).thenReturn(expectedComments);

        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(commodity1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedComments, response.getBody());
    }

    @Test
    public void testGetCommodityCommentWithNoComments() {
        when(baloot.getCommentsForCommodity(anyInt())).thenReturn(new ArrayList<>());

        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(commodity1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    public void testGetCommodityCommentWithInvalidCommodityId() {
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment("invalidId");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testSearchCommoditiesByName() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "name");
        input.put("searchValue", "SampleName");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByName("SampleName")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesByNameWithNoCommodities() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "name");
        input.put("searchValue", "SampleName");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName("SampleName")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesByCategory() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "category");
        input.put("searchValue", "SampleCategory");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByCategory("SampleCategory")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesByCategoryWithNoCommodities() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "category");
        input.put("searchValue", "SampleCategory");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();

        when(baloot.filterCommoditiesByCategory("SampleCategory")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesByProvider() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "provider");
        input.put("searchValue", "SampleProvider");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByProviderName("SampleProvider")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesByProviderWithNoProvider() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "provider");
        input.put("searchValue", "SampleProvider");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();

        when(baloot.filterCommoditiesByProviderName("SampleProvider")).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommodities, response.getBody());
    }

    @Test
    public void testSearchCommoditiesWithInvalidSearchOption() {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "invalidOption");
        input.put("searchValue", "InvalidValue");

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    public void testGetSuggestedCommoditiesSuccess() throws NotExistentCommodity {
        ArrayList<Commodity> expectedSuggestedCommodities = new ArrayList<>();
        expectedSuggestedCommodities.add(commodity1);
        expectedSuggestedCommodities.add(commodity2);

        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);
        when(baloot.suggestSimilarCommodities(commodity1)).thenReturn(expectedSuggestedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSuggestedCommodities, response.getBody());
    }

    @Test
    public void testGetSuggestedCommoditiesSuccessWithNoCommodities() throws NotExistentCommodity {
        ArrayList<Commodity> expectedSuggestedCommodities = new ArrayList<>();

        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);
        when(baloot.suggestSimilarCommodities(commodity1)).thenReturn(expectedSuggestedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSuggestedCommodities, response.getBody());
    }

    @Test
    public void testGetSuggestedCommoditiesNotExistentCommodity() throws NotExistentCommodity {
        when(baloot.getCommodityById(anyString())).thenThrow(NotExistentCommodity.class);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity1.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    // API Tests

    @Test
    public void testGetCommoditiesAPI() throws Exception{
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);
        when(baloot.getCommodities()).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(commodity1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(commodity2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commodity2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(commodity2.getPrice()));
    }

    @Test
    public void testGetCommoditiesWithNoCommoditiesAPI() throws Exception{
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(baloot.getCommodities()).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testGetCommodityAPI() throws Exception {
        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(commodity1.getPrice()));
    }

    @Test
    public void testGetCommodityNotExistentCommodityAPI() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testRateCommoditySuccessAPI() throws Exception {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "5");

        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/rate", commodity1.getId())
                        .contentType("application/json")
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("rate added successfully!"));
    }

    @Test
    public void testRateCommodityNotExistentCommodityAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");
        input.put("rate", "5");

        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/rate", commodity1.getId())
                        .contentType("application/json")
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testRateCommodityInvalidRateRangeAPI() throws Exception {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "10");

        Commodity commodity = mock(Commodity.class);

        when(baloot.getCommodityById(anyString())).thenReturn(commodity);
        doThrow(new InvalidRateRange()).when(commodity).addRate(anyString(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/rate", commodity1.getId())
                        .contentType("application/json")
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string(INVALID_RATE_RANGE));
    }

    @Test
    public void testRateCommodityInvalidRatingInputAPI() throws Exception {
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", "invalidRating");

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/rate", commodity1.getId())
                        .contentType("application/json")
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("For input string: \"invalidRating\""));
    }

    @Test
    public void testAddCommodityCommentSuccessAPI() throws Exception {
        String username = "testUser";
        String commentText = "Sample comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        int commentId = 1;
        when(baloot.generateCommentId()).thenReturn(commentId);
        when(baloot.getUserById(username)).thenReturn(user);
        doNothing().when(baloot).addComment(commentCaptor.capture());
        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/comment", commodity1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("comment added successfully!"));
    }

    @Test
    public void testAddCommodityCommentUserNotExistentAPI() throws Exception {
        String username = "nonExistentUser";
        String commentText = "This is a comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);
        when(baloot.generateCommentId()).thenReturn(1);
        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());
        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/comment", commodity1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testAddCommodityCommentInvalidCommodityIdAPI() throws Exception {
        String username = "nonExistentUser";
        String commentText = "This is a comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);
        when(baloot.generateCommentId()).thenReturn(1);
        when(baloot.getUserById(username)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/{id}/comment", "InvalidId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetCommodityCommentSuccessAPI() throws Exception {
        ArrayList<Comment> expectedComments = new ArrayList<>();
        expectedComments.add(comment1);
        expectedComments.add(comment2);

        when(baloot.getCommentsForCommodity(anyInt())).thenReturn(expectedComments);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/comment", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(comment1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userEmail").value(comment1.getUserEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(comment1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].commodityId").value(comment1.getCommodityId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value(comment1.getText()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(comment2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userEmail").value(comment2.getUserEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value(comment2.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].commodityId").value(comment2.getCommodityId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value(comment2.getText()));
    }

    @Test
    public void testGetCommodityCommentWithNoCommentsAPI() throws Exception {
        when(baloot.getCommentsForCommodity(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/comment", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testGetCommodityCommentWithInvalidCommodityIdAPI() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/comment", "invalidId"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testSearchCommoditiesByNameAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "name");
        input.put("searchValue", "SampleName");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByName("SampleName")).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(commodity1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(commodity2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commodity2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(commodity2.getPrice()));
    }

    @Test
    public void testSearchCommoditiesWithInvalidSearchOptionAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "invalidOption");
        input.put("searchValue", "InvalidValue");

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testSearchCommoditiesByCategoryAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "category");
        input.put("searchValue", "SampleCategory");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByCategory("SampleCategory")).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(commodity1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(commodity2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commodity2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(commodity2.getPrice()));
    }

    @Test
    public void testSearchCommoditiesByCategoryWithNoCommoditiesAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "category");
        input.put("searchValue", "SampleCategory");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();

        when(baloot.filterCommoditiesByCategory("SampleCategory")).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testSearchCommoditiesByProviderAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "provider");
        input.put("searchValue", "SampleProvider");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        expectedCommodities.add(commodity1);
        expectedCommodities.add(commodity2);

        when(baloot.filterCommoditiesByProviderName("SampleProvider")).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(commodity1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(commodity2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commodity2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(commodity2.getPrice()));
    }

    @Test
    public void testSearchCommoditiesByProviderWithNoProviderAPI() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "provider");
        input.put("searchValue", "SampleProvider");

        ArrayList<Commodity> expectedCommodities = new ArrayList<>();

        when(baloot.filterCommoditiesByProviderName("SampleProvider")).thenReturn(expectedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testGetSuggestedCommoditiesSuccessAPI() throws Exception {
        ArrayList<Commodity> expectedSuggestedCommodities = new ArrayList<>();
        expectedSuggestedCommodities.add(commodity1);
        expectedSuggestedCommodities.add(commodity2);

        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);
        when(baloot.suggestSimilarCommodities(commodity1)).thenReturn(expectedSuggestedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/suggested", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(commodity1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commodity1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(commodity1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(commodity2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commodity2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(commodity2.getPrice()));
    }

    @Test
    public void testGetSuggestedCommoditiesSuccessWithNoCommoditiesAPI() throws Exception {
        ArrayList<Commodity> expectedSuggestedCommodities = new ArrayList<>();

        when(baloot.getCommodityById(anyString())).thenReturn(commodity1);
        when(baloot.suggestSimilarCommodities(commodity1)).thenReturn(expectedSuggestedCommodities);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/suggested", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testGetSuggestedCommoditiesNotExistentCommodityAPI() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(NotExistentCommodity.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/commodities/{id}/suggested", commodity1.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
