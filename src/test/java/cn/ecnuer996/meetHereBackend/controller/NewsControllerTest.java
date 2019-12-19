package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.service.NewsService;
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
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class NewsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private NewsService newsService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAllNews() {
    }

    @Test
    void addNews() {
    }

    @Test
    @DisplayName("获取单条新闻")
    void getNews() throws Exception {
        News news=new News();
        when(newsService.getSingleNews(1)).thenReturn(news);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/news")
        .param("newsId","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("请求成功"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("获取单条新闻失败")
    void getNewsWhenError() throws Exception {
        when(newsService.getSingleNews(1)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/news")
                .param("newsId","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("500"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("请求失败"));
    }

    @Test
    @DisplayName("获取新闻图片")
    void getNewsImages() throws Exception {
        List<String> fakeImages=new ArrayList<>();
        when(newsService.getNewsImages(1)).thenReturn(fakeImages);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/news-images")
        .param("newsId","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("请求成功"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("获取新闻图片失败")
    void getNewsImagesWhenError() throws Exception {
        when(newsService.getNewsImages(1)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/news-images")
                .param("newsId","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("500"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("请求失败"));
    }
}