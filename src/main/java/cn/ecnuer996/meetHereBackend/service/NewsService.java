package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ManagerMapper;
import cn.ecnuer996.meetHereBackend.dao.NewsImageMapper;
import cn.ecnuer996.meetHereBackend.dao.NewsMapper;
import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
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

    public void getNewsDetail(JSONObject response,int newsId){
        News news=newsDao.selectByPrimaryKey(newsId);
        List<String> images=newsImageDao.selectNewsImagesByNewsId(newsId);
        List<String> urls=new ArrayList<>();
        for(String image:images){
            urls.add(FilePathUtil.URL_NEWS_IMAGE_PREFIX+image);
        }
        response.put("id",news.getId());
        Manager manager=managerDao.selectByPrimaryKey(news.getManagerId());
        response.put("manager",manager.getName());
        response.put("managerAvatar",FilePathUtil.URL_MANAGER_AVATAR_PREFIX+manager.getAvatar());
        response.put("title",news.getTitle());
        response.put("content",news.getContent());
        response.put("time",format.format(news.getTime()));
        response.put("images",urls);
    }

    public String getNewsCover(int newsId){
        return FilePathUtil.URL_NEWS_IMAGE_PREFIX+newsImageDao.selectNewsCoverByNewsId(newsId);
    }

}
