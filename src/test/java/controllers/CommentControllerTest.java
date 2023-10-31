package controllers;
import exceptions.InvalidVote;
import exceptions.NotExistentComment;
import model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    Baloot baloot;

    @Mock
    Comment comment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        commentController = new CommentController();
        commentController.setBaloot(baloot);
    }

    @Test
    public void testLikeCommentSuccess() throws NotExistentComment, InvalidVote {
        int commentId = 1;
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenReturn(comment);

        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());

        verify(comment).addUserVote(username, "like");
    }

    @Test
    public void testLikeCommentNotExistentComment() throws NotExistentComment {
        int commentId = 1;
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");

        when(baloot.getCommentById(commentId)).thenThrow(NotExistentComment.class);

        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        //assertEquals("Comment not found", response.getBody());
    }

    @Test
    public void testLikeCommentInvalidVote() throws NotExistentComment, InvalidVote {
        int commentId = 1;
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");

        when(baloot.getCommentById(commentId)).thenReturn(comment);

        doThrow(InvalidVote.class).when(comment).addUserVote("testUser", "like");

        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //assertEquals("Invalid vote", response.getBody());

    }
    @Test
    public void testDislikeCommentSuccess() throws NotExistentComment, InvalidVote {
        int commentId = 1;
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenReturn(comment);

        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());

        verify(comment).addUserVote(username, "dislike");
    }

    @Test
    public void testDislikeCommentNotExistentComment() throws NotExistentComment {
        int commentId = 1;
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");

        when(baloot.getCommentById(commentId)).thenThrow(NotExistentComment.class);

        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        //assertEquals("Comment not found", response.getBody());
    }

    @Test
    public void testDislikeCommentInvalidVote() throws NotExistentComment, InvalidVote {
        int commentId = 1;
        Map<String, String> input = new HashMap<>();
        input.put("username", "testUser");

        when(baloot.getCommentById(commentId)).thenReturn(comment);

        doThrow(InvalidVote.class).when(comment).addUserVote("testUser", "dislike");

        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //assertEquals("Invalid vote", response.getBody());
    }

}
