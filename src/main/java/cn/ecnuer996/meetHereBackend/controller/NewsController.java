package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.News;
import cn.ecnuer996.meetHereBackend.service.NewsService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@RestController
@Api(tags = "新闻相关接口")
public class NewsController {
    public NewsService newsService;

    @Autowired
    public void setNewsService(NewsService newsService) {
        this.newsService = newsService;
    }

    @ApiOperation("分页查询所有新闻信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="news")
    public JSONObject getAllNews(@RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page){
        ArrayList<News> pre_news = newsService.getAllNews();
        int num_of_pages = Math.max((int) Math.ceil(pre_news.size() / (double) segment), 1);
        ArrayList<News> news = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_news.size()); ++i){
            news.add(pre_news.get(i));
        }
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("messages","查询成功");
        response.put("num_of_pages", num_of_pages);
        response.put("result",news);
        return response;
    }

    @ApiOperation("新增新闻")
    @ApiImplicitParams({ @ApiImplicitParam(name = "managerId", value = "管理员", required = true),
                         @ApiImplicitParam(name = "time", value = "时间", required = true),
                         @ApiImplicitParam(name = "title", value = "标题", required = true),
                         @ApiImplicitParam(name = "content", value = "内容", required = true)})
    @GetMapping(value="add_news")
    public JSONObject addNews(@RequestParam("managerId")Integer managerId,
                              @RequestParam("time")String time,
                              @RequestParam("title")String title,
                              @RequestParam("content")String content){
        News news = new News();
        news.setManagerId(managerId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            news.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        news.setTitle(title);
        news.setContent(content);
        newsService.addNews(news);
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("messages","新增成功");
        return response;
    }
}
