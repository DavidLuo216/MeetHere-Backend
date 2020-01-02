package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.CommentMapper;
import cn.ecnuer996.meetHereBackend.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @MockBean
    CommentMapper commentDao;

    @Test
    void getWholeComments() {
        commentService.getWholeComments();
        verify(commentDao, times(1)).getWholeComments();
    }

    @Test
    void getVenueComments() {
        commentService.getVenueComments(1);
        verify(commentDao, times(1)).getVenueComments(1);
    }

    @Test
    void getNewsComments() {
        commentService.getNewsComments(1);
        verify(commentDao, times(1)).getNewsComments(1);
    }

    @Test
    void detectComment() {
        when(commentDao.detectComment(anyInt())).thenReturn(null);
        commentService.deleteComment(1);
    }

    @Test
    void addGlobalComment() {
        commentService.addGlobalComment(new Comment());
        verify(commentDao, times(1)).addGlobalComment(isA(Comment.class));
    }

    @Test
    void getNextId() {
        commentService.getNextId("1");
        verify(commentDao, times(1)).getNextId(isA(String.class));
    }

    @Test
    void modifyUserComment() {
        commentService.modifyUserComment(1,1,"");
        verify(commentDao, times(1)).modifyUserComment(isA(Integer.class), isA(Integer.class), isA(String.class));
    }

}
