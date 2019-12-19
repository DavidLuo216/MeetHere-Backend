package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Comment;
import cn.ecnuer996.meetHereBackend.service.CommentService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@CrossOrigin
@RestController
@Api(tags = "评论相关接口")
public class CommentController {

    private CommentService commentService;

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @ApiOperation("获取全局评论")
    @GetMapping(value="/global-comments")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
        @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)
    })
    public JSONObject getGlobalComments(
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        JSONObject response = new JSONObject();
        ArrayList<Comment> wholeComments = commentService.getWholeComments();
        Integer len = wholeComments.size();
        if(len > 0) {
            response.put("code", 200);
            response.put("message", "查找成功");
            response.put("num_of_pages", Math.max((int) Math.ceil(len / (double) segment), 1));

            ArrayList<Comment> result = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                result.add(wholeComments.get(i));
            }

            response.put("result", result);
        }
        else {
            response.put("code", 404);
            response.put("message", "列表为空");
            response.put("result", null);
        }
        return response;
    }

    @ApiOperation("获取场馆评论")
    @GetMapping(value="/venue-comments")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "venueId", value = "场馆编号", required = true),
        @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
        @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)
    })
    public JSONObject getVenueComments(
        @RequestParam("venueId")Integer link,
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        JSONObject response = new JSONObject();

        ArrayList<Comment> venueComments = commentService.getVenueComments(link);
        Integer len = venueComments.size();
        if(len > 0) {
            response.put("code", 200);
            response.put("message", "查找成功");
            response.put("num_of_pages", Math.max((int) Math.ceil(len / (double) segment), 1));

            ArrayList<Comment> result = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                result.add(venueComments.get(i));
            }

            response.put("result", result);
        }
        else {
            response.put("code", 404);
            response.put("message", "列表为空");
            response.put("result", null);
        }

        return response;
    }

    @ApiOperation("获取新闻评论")
    @GetMapping(value="/news-comments")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "newsId", value = "新闻编号", required = true),
        @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
        @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)
    })
    public JSONObject getNewsComments(
        @RequestParam("newsId")Integer link,
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        JSONObject response = new JSONObject();

        ArrayList<Comment> newsComments = commentService.getNewsComments(link);
        Integer len = newsComments.size();
        if(len > 0) {
            response.put("code", 200);
            response.put("message", "查找成功");
            response.put("num_of_pages", Math.max((int) Math.ceil(len / (double) segment), 1));

            ArrayList<Comment> result = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                result.add(newsComments.get(i));
            }

            response.put("result", result);
        }
        else {
            response.put("code", 404);
            response.put("message", "列表为空");
            response.put("result", null);
        }

        return response;
    }

    @ApiOperation("删除评论")
    @GetMapping(value="/delete-comment")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "评论编号", required = true)
    })
    public JSONObject deleteComment(
        @RequestParam("id")Integer id) {
        JSONObject response = new JSONObject();
        boolean judge = commentService.detectComment(id);
        if(judge) {
            response.put("code", 200);
            response.put("message", "删除成功");
            commentService.deleteComment(id);
        }
        else {
            response.put("code", 400);
            response.put("message", "无此评论");
        }
        return response;
    }

    /* 测试用代码，一键恢复被删除的评论，仅提供测试API时的便利，并不上线 */
    @ApiOperation("恢复评论")
    @GetMapping(value="/discover-comments")
    public void discoverComment() {
        commentService.discoverComments();
    }

    @ApiOperation("新增全局评论")
    @GetMapping(value="/add-global-comment")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
        @ApiImplicitParam(name = "content", value = "评论内容", required = true)
    })
    public JSONObject addGlobalComment(
        @RequestParam("userId")Integer userId,
        @RequestParam("content")String content) {
        JSONObject response = new JSONObject();
        if(userId > 90000) {
            response.put("code", 200);
            response.put("message", "发布成功");
            Comment comment = new Comment(commentService.getNextId("1"), "1", userId, content, 1);
            commentService.addGlobalComment(comment);
        }
        else{
            response.put("code", 400);
            response.put("message", "无此权限");
        }
        return response;
    }

    @ApiOperation("新增场馆评论")
    @GetMapping(value="/add-venue-comment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true),
            @ApiImplicitParam(name = "venueId", value = "场馆编号", required = true)
    })
    public JSONObject addVenueComment(
            @RequestParam("userId")Integer userId,
            @RequestParam("content")String content,
            @RequestParam("venueId")Integer link) {
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("message", "评论成功");
        Comment comment = new Comment(commentService.getNextId("2"), "2", userId, content, link);
        commentService.addGlobalComment(comment);
        return response;
    }

    @ApiOperation("新增新闻评论")
    @GetMapping(value="/add-news-comment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true),
            @ApiImplicitParam(name = "newsId", value = "新闻编号", required = true)
    })
    public JSONObject addNewsComment(
            @RequestParam("userId")Integer userId,
            @RequestParam("content")String content,
            @RequestParam("newsId")Integer link) {
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("message", "评论成功");
        Comment comment = new Comment(commentService.getNextId("3"), "3", userId, content, link);
        commentService.addGlobalComment(comment);
        return response;
    }

}
