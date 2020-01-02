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
        ArrayList<Comment> comments = commentDao.detectComment(id);
        return comments.size() == 1;
    }

    public void deleteComment(Integer id) {
        commentDao.deleteComment(id);
    }

    public boolean addGlobalComment(Comment comment) {
        commentDao.addGlobalComment(comment);
        return true;
    }

    public Integer getNextId(String type) {
        return Integer.parseInt(type) * 10000 + commentDao.getNextId(type) + 1;
    }

    public boolean modifyUserComment(Integer userId, Integer commentId, String content) {
        commentDao.modifyUserComment(userId, commentId, content);
        return true;
    }

}
