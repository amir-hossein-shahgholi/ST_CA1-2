package model;

import exceptions.InvalidCreditRange;
import exceptions.InvalidVote;
import model.Comment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = new Comment(
                1,
                "sample@ut.ac.ir",
                "testUser",
                1,
                "Sample comment");
    }

    @AfterEach
    public void tearDown() {
        comment = null;
    }

    @Test
    public void testGetCurrentDate() {
        String commentDate = comment.getCurrentDate();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals(commentDate, dateFormat.format(currentDate));
    }
    @ParameterizedTest
    @CsvSource({
            "like, 1, 0",
            "dislike, 0, 1",
    })
    public void testAddUserVoteSuccessfully(String vote, int expected_likes, int expected_dislikes) throws InvalidVote {
        comment.addUserVote("User1", vote);
        assertEquals(expected_likes, comment.getLike());
        assertEquals(expected_dislikes, comment.getDislike());
        assertEquals(vote, comment.getUserVote().get("User1"));
    }

    @ParameterizedTest
    @CsvSource({
            "like, like, 2, 0",
            "dislike, dislike, 0, 2",
            "like, dislike, 1, 1"
    })
    public void testAddedMultipleUserVotesSuccessfully(String vote1, String vote2, int expected_likes,
                                                       int expected_dislikes) throws InvalidVote {
        comment.addUserVote("User1", vote1);
        comment.addUserVote("User2", vote2);
        assertEquals(expected_likes, comment.getLike());
        assertEquals(expected_dislikes, comment.getDislike());
        assertEquals(vote1, comment.getUserVote().get("User1"));
        assertEquals(vote2, comment.getUserVote().get("User2"));
    }

    @ParameterizedTest
    @CsvSource({
            "like, dislike, 0, 1",
            "dislike, like, 1, 0",
            "dislike, dislike, 0, 1",
            "like, like, 1, 0",
    })
    public void testChangeVoteSuccessfully(String vote1, String vote2, int expected_likes,
                                           int expected_dislikes) throws InvalidVote {
        comment.addUserVote("User1", vote1);
        comment.addUserVote("User1", vote2);
        assertEquals(expected_likes, comment.getLike());
        assertEquals(expected_dislikes, comment.getDislike());
        assertEquals(vote2, comment.getUserVote().get("User1"));
    }
    @ParameterizedTest
    @ValueSource(strings = { "likee", "dislikee", " "  })
    public void testAddUserVoteWithInvalidVoteShouldFail(String vote) {
        assertThrows(InvalidVote.class, () -> comment.addUserVote("User1", vote));
    }
}
