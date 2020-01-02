package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Comment;
import cn.ecnuer996.meetHereBackend.service.CommentService;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
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
import java.util.HashMap;
import java.util.Map;

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
    public JsonResult getGlobalComments(
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        ArrayList<Comment> wholeComments = commentService.getWholeComments();
        Integer len = wholeComments.size();
        if(len > 0) {
            Map<String,Object> result=new HashMap<>(2);
            ArrayList<Comment> comments = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                comments.add(wholeComments.get(i));
            }
            result.put("comments",comments);
            result.put("num_of_pages",Math.max((int) Math.ceil(len / (double) segment), 1));
            return new JsonResult(result,"查找成功");
        }
        else {
            return new JsonResult(JsonResult.NOT_FOUND,"列表为空");
        }
    }

    @ApiOperation("获取场馆评论")
    @GetMapping(value="/venue-comments")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "venueId", value = "场馆编号", required = true),
        @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
        @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)
    })
    public JsonResult getVenueComments(
        @RequestParam("venueId")Integer link,
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        ArrayList<Comment> venueComments = commentService.getVenueComments(link);
        Integer len = venueComments.size();
        if(len > 0) {
            Map<String,Object> result=new HashMap<>(2);
            ArrayList<Comment> comments = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                comments.add(venueComments.get(i));
            }
            result.put("comments",comments);
            result.put("num_of_pages",Math.max((int) Math.ceil(len / (double) segment), 1));
            return new JsonResult(result,"查找成功");
        }
        else {
            return new JsonResult(JsonResult.NOT_FOUND,"列表为空");
        }
    }

    @ApiOperation("获取新闻评论")
    @GetMapping(value="/news-comments")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "newsId", value = "新闻编号", required = true),
        @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
        @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)
    })
    public JsonResult getNewsComments(
        @RequestParam("newsId")Integer link,
        @RequestParam("segment")Integer segment,
        @RequestParam("page")Integer page) {
        ArrayList<Comment> newsComments = commentService.getNewsComments(link);
        Integer len = newsComments.size();
        if(len > 0) {
            Map<String,Object> result=new HashMap<>(2);
            ArrayList<Comment> comments = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, len); ++i){
                comments.add(newsComments.get(i));
            }
            result.put("comments",comments);
            result.put("num_of_pages",Math.max((int) Math.ceil(len / (double) segment), 1));
            return new JsonResult(result,"查找成功");
        }
        else {
            return new JsonResult(JsonResult.NOT_FOUND,"列表为空");
        }
    }

    @ApiOperation("删除评论")
    @GetMapping(value="/delete-comment")
        @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "评论编号", required = true)
    })
    public JsonResult deleteComment(
        @RequestParam("id")Integer id) {
        boolean judge = commentService.detectComment(id);
        if(judge) {
            commentService.deleteComment(id);
            return new JsonResult("删除成功");
        }
        else {
            return new JsonResult(JsonResult.NOT_FOUND,"无此评论");
        }
    }

    /*
    不上线功能，完美无瑕，不作测试，切勿删除
    @ApiOperation("恢复评论")
    @GetMapping(value="/discover-comments")
    public JsonResult discoverComment() {
        commentService.discoverComments();
        return new JsonResult();
    }
     */

    @ApiOperation("新增全局评论")
    @GetMapping(value="/add-global-comment")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
        @ApiImplicitParam(name = "content", value = "评论内容", required = true)
    })
    public JsonResult addGlobalComment(
        @RequestParam("userId")Integer userId,
        @RequestParam("content")String content) {
        Comment comment = new Comment(commentService.getNextId("1"), "1", userId, content, 1);
        commentService.addGlobalComment(comment);
        return new JsonResult("发布成功");
    }

    @ApiOperation("新增场馆评论")
    @GetMapping(value="/add-venue-comment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true),
            @ApiImplicitParam(name = "venueId", value = "场馆编号", required = true)
    })
    public JsonResult addVenueComment(
            @RequestParam("userId")Integer userId,
            @RequestParam("content")String content,
            @RequestParam("venueId")Integer link) {
        Comment comment = new Comment(commentService.getNextId("2"), "2", userId, content, link);
        commentService.addGlobalComment(comment);
        return new JsonResult("评论成功");
    }

    @ApiOperation("新增新闻评论")
    @GetMapping(value="/add-news-comment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true),
            @ApiImplicitParam(name = "newsId", value = "新闻编号", required = true)
    })
    public JsonResult addNewsComment(
            @RequestParam("userId")Integer userId,
            @RequestParam("content")String content,
            @RequestParam("newsId")Integer link) {
        Comment comment = new Comment(commentService.getNextId("3"), "3", userId, content, link);
        commentService.addGlobalComment(comment);
        return new JsonResult("评论成功");
    }

    @ApiOperation("修改用户评论")
    @GetMapping(value="/modify-comment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论编号", required = true),
            @ApiImplicitParam(name = "userId", value = "用户编号", required = true),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true)
    })
    public JsonResult modifyUserComment(
            @RequestParam("commentId")Integer newsId,
            @RequestParam("userId")Integer userId,
            @RequestParam("content")String content) {
        commentService.modifyUserComment(userId, newsId, content);
        return new JsonResult("修改成功");
    }

}
