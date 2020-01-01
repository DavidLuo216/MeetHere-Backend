package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.CommentMapper;
import cn.ecnuer996.meetHereBackend.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Ethereality
 */
@Service("CommentService")
public class CommentService {

    private CommentMapper commentDao;

    @Autowired
    public void setCommentDao(CommentMapper commentDao) {
        this.commentDao = commentDao;
    }

    public ArrayList<Comment> getWholeComments() {
        return commentDao.getWholeComments();
    }

    public ArrayList<Comment> getVenueComments(Integer venueId) {
        return commentDao.getVenueComments(venueId);
    }

    public ArrayList<Comment> getNewsComments(Integer newsId) {
        return commentDao.getNewsComments(newsId);
    }

    public boolean detectComment(Integer id) {
        ArrayList<Comment> comments =  commentDao.detectComment(id);
        return comments.size() == 1;
    }

    public void deleteComment(Integer id) {
        commentDao.deleteComment(id);
    }

    public void discoverComments() {
        commentDao.discoverComments();
    }

    public void addGlobalComment(Comment comment) {
        commentDao.addGlobalComment(comment);
    }

    public Integer getNextId(String type) {
        return Integer.parseInt(type) * 10000 + commentDao.getNextId(type) + 1;
    }

    /* Functions for SpringBoot Testing are Shown as Follows */
    public Comment getSingleComment (Integer id) {
        return commentDao.selectByPrimaryKey(id);
    }

}
