package model;

import exceptions.InvalidVote;
import model.Comment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class CommentTest {

    private Comment comment;

    @Before
    public void setUp() {
        comment = new Comment(1, "sample@ut.ac.ir", "testUser", 1, "Sample comment");
    }

    @Test
    public void testGetCurrentDate() {
        //TODO
    }

    @Test
    public void testAddUserVoteSuccessfully() {
        try {
            comment.addUserVote("User1", "like");
            comment.addUserVote("User2", "dislike");
            assertEquals(1, comment.getLike());
            assertEquals(1, comment.getDislike());
            assertEquals("like", comment.getUserVote().get("User1"));
            assertEquals("dislike", comment.getUserVote().get("User2"));
        } catch (InvalidVote e){
            fail("InvalidVote exception should not be thrown.");
        }
    }

    @Test
    public void testAddUserVoteMultipleVotesSuccessfully() {
        try {
            comment.addUserVote("User1", "like");
            comment.addUserVote("User2", "like");
            assertEquals(2, comment.getLike());
            assertEquals(0, comment.getDislike());
            assertEquals("like", comment.getUserVote().get("User1"));
            assertEquals("like", comment.getUserVote().get("User2"));
        }catch (InvalidVote e){
            fail("InvalidVote exception should not be thrown.");
        }
    }
    @Test
    public void testAddUserVoteWithInvalidVoteShouldFail() {
        try {
            comment.addUserVote("User1", "likee");
            fail("InvalidVote exception should be thrown.");
        }catch (InvalidVote e){
        }
    }
}
