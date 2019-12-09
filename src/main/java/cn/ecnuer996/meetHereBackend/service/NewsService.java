package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.NewsMapper;
import cn.ecnuer996.meetHereBackend.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NewsService {
    @Autowired
    private NewsMapper newsDao;

    public void addNews(News news) {
        newsDao.insert(news);
    }

    public ArrayList<News> getAllNews() {
        //return new ArrayList<>();
        return newsDao.selectAllVenues();
    }
}
