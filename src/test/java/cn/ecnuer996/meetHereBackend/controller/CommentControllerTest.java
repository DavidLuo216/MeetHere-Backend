package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Comment;
import cn.ecnuer996.meetHereBackend.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CommentService commentService;
    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("获取全局评论成功")
    void testGlobalCommentsSuccess() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        when(commentService.getWholeComments()).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/global-comments")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("查找成功"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("获取全局评论失败")
    void testGlobalCommentsError() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        when(commentService.getWholeComments()).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/global-comments")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("列表为空"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

    @Test
    @DisplayName("获取场馆评论成功")
    void testVenueCommentsSuccess() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        when(commentService.getVenueComments(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/venue-comments")
                .param("venueId","1")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("查找成功"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("获取场馆评论失败")
    void testVenueCommentsError() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        when(commentService.getVenueComments(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/global-comments")
                .param("venueId","1")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("列表为空"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

    @Test
    @DisplayName("获取新闻评论成功")
    void testNewsCommentsSuccess() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        when(commentService.getNewsComments(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/news-comments")
                .param("newsId","1")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("查找成功"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("获取新闻评论失败")
    void testNewsCommentsError() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        when(commentService.getVenueComments(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/global-comments")
                .param("venueId","1")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("列表为空"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

}
