package controllers;
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
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommoditiesControllerTest {

    private CommoditiesController commoditiesController;

    @Mock
    Baloot baloot;

    private
    Commodity commodity1;
    Commodity commodity2;
    Comment comment1;
    Comment comment2;
    User user;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        commoditiesController = new CommoditiesController();
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
}
