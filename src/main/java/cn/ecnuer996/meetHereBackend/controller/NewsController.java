package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.model.NewsImageKey;
import cn.ecnuer996.meetHereBackend.service.NewsService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "新闻相关接口")
public class NewsController {
    private NewsService newsService;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public void setNewsService(NewsService newsService) {
        this.newsService = newsService;
    }

    @ApiOperation("新闻一览")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/all-news")
    public JSONObject getAllNews(@RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page){
        ArrayList<News> preNews = newsService.getAllNews();
        int numOfPages = Math.max((int) Math.ceil(preNews.size() / (double) segment), 1);
        ArrayList<News> news = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, preNews.size()); ++i){
            news.add(preNews.get(i));
        }
        JSONObject response = new JSONObject();
        JSONObject result=new JSONObject();
        response.put("code",200);
        response.put("messages","查询成功");
        result.put("numOfPages",numOfPages);
        result.put("newsList",news);
        response.put("result",result);
        return response;
    }

    @ApiOperation("新增新闻")
    @ApiImplicitParams({ @ApiImplicitParam(name = "managerId", value = "管理员ID", required = true),
                         @ApiImplicitParam(name = "time", value = "时间", required = true),
                         @ApiImplicitParam(name = "title", value = "标题", required = true),
                         @ApiImplicitParam(name = "content", value = "内容", required = true)})
    @PostMapping(value="/add-news")
    public JSONObject addNews(@RequestParam("managerId")Integer managerId,
                              @RequestParam("time")String time,
                              @RequestParam("title")String title,
                              @RequestParam("content")String content,
                              @RequestParam("images")MultipartFile[] images){
        JSONObject response = new JSONObject();
        News news = new News();
        news.setManagerId(managerId);
        try {
            news.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        news.setTitle(title);
        news.setContent(content);
        newsService.addNews(news);
        News newsInserted=newsService.getNewsByTitle(title);
        if(newsInserted==null){
            response.put("code",500);
            response.put("message","插入数据库失败");
            return response;
        }
        int newsId=newsInserted.getId();
        for(MultipartFile image:images){
            String fileName=image.getOriginalFilename();
            String fileType=fileName.substring(fileName.lastIndexOf('.'));
            String destFile=FilePathUtil.LOCAL_NEWS_IMAGE_PREFIX+System.currentTimeMillis()+fileType;
            try{
                image.transferTo(new File(destFile));
            } catch (IOException e) {
                e.printStackTrace();
                response.put("code",500);
                response.put("message","保存图片失败");
                return response;
            }
            NewsImageKey newsImageKey=new NewsImageKey();
            newsImageKey.setNewsId(newsId);
            newsImageKey.setImage(destFile);
            newsService.addNewsImage(newsImageKey);
        }
        JSONObject result=new JSONObject();
        result.put("id",newsId);
        response.put("result",result);
        response.put("code",200);
        response.put("message","新增成功");
        return response;
    }

    @ApiOperation("用id获取单个新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsId",value = "新闻ID", required = true)
    })
    @GetMapping(value = "/news")
    public JSONObject getNews(@RequestParam int newsId){
        JSONObject response=new JSONObject();
        JSONObject result=new JSONObject();
        News news=newsService.getSingleNews(newsId);
        if(news==null){
            response.put("code",500);
            response.put("message","请求失败");
        }else{
            result.put("news",news);
            response.put("result",result);
            response.put("code",200);
            response.put("message","请求成功");
        }
        return response;
    }

    @ApiOperation("获取单个新闻的所有图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsId",value = "新闻ID", required = true)
    })
    @GetMapping(value = "/news-images")
    public JSONObject getNewsImages(@RequestParam int newsId){
        JSONObject response=new JSONObject();
        JSONObject result=new JSONObject();
        List<String> images=newsService.getNewsImages(newsId);
        if(images==null){
            response.put("code",500);
            response.put("message","请求失败");
        }else{
            result.put("images",images);
            response.put("result",result);
            response.put("code",200);
            response.put("message","请求成功");
        }
        return response;
    }
}
