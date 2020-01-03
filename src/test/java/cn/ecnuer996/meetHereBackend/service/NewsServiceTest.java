package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.NewsImageMapper;
import cn.ecnuer996.meetHereBackend.dao.NewsMapper;
import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.model.NewsImageKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class NewsServiceTest {

    @Autowired
    NewsService newsService;

    @MockBean
    NewsMapper newsDao;

    @MockBean
    NewsImageMapper newsImageDao;

    @Test
    void addNews() {
        newsService.addNews(new News());
        verify(newsDao, times(1)).insert(isA(News.class));
    }

    @Test
    void getAllNews() {
        newsService.getAllNews();
        verify(newsDao, times(1)).selectAllNews();
    }

    @Test
    void getSingleNews() {
        when(newsService.getSingleNews(anyInt())).thenReturn(new News());
        newsService.getSingleNews(1);
        verify(newsDao, times(1)).selectByPrimaryKey(1);
    }

    @Test
    void getNewsImages() {
        List<String> images = new ArrayList<>();
        images.add("122");
        images.add("2233");
        when(newsImageDao.selectNewsImagesByNewsId(1)).thenReturn(images);
        newsService.getNewsImages(1);
        verify(newsImageDao, times(1)).selectNewsImagesByNewsId(1);
    }

    @Test
    void getNewsByTitle() {
        newsService.getNewsByTitle("222");
        verify(newsDao, times(1)).selectByTitle("222");
    }

    @Test
    void addNewsImages() {
        NewsImageKey newsImageKey = new NewsImageKey();
        newsImageKey.setNewsId(1);
        newsImageKey.setImage("2");
        newsService.addNewsImage(newsImageKey);
        verify(newsImageDao, times(1)).insert(isA(NewsImageKey.class));
    }

}
