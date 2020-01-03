package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ManagerMapper;
import cn.ecnuer996.meetHereBackend.dao.NewsImageMapper;
import cn.ecnuer996.meetHereBackend.dao.NewsMapper;
import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.model.NewsImageKey;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    private NewsMapper newsDao;
    private NewsImageMapper newsImageDao;
    private ManagerMapper managerDao;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public void setNewsDao(NewsMapper newsDao) {
        this.newsDao = newsDao;
    }

    @Autowired
    public void setNewsImageDao(NewsImageMapper newsImageDao) {
        this.newsImageDao = newsImageDao;
    }

    @Autowired
    public void setManagerDao(ManagerMapper managerDao) {
        this.managerDao = managerDao;
    }

    public void addNews(News news) {
        newsDao.insert(news);
    }

    public ArrayList<News> getAllNews() {
        return newsDao.selectAllNews();
    }

    public News getSingleNews(int newsId){
        return newsDao.selectByPrimaryKey(newsId);
    }

    public List<String> getNewsImages(int newsId){
        List<String> images=newsImageDao.selectNewsImagesByNewsId(newsId);
        List<String> urls=new ArrayList<>();
        for(String image:images){
            urls.add(FilePathUtil.URL_NEWS_IMAGE_PREFIX+image);
        }
        return urls;
    }

    public News getNewsByTitle(String title){
        return newsDao.selectByTitle(title);
    }

    public int addNewsImage(NewsImageKey newsImageKey){
        return newsImageDao.insert(newsImageKey);
    }

}
