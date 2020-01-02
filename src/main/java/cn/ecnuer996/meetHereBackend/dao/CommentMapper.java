package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CommentMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    /* Personal Functions Begin */

    ArrayList<Comment> getWholeComments();

    ArrayList<Comment> getVenueComments(Integer link);

    ArrayList<Comment> getNewsComments(Integer link);

    ArrayList<Comment> detectComment(Integer id);

    void deleteComment(Integer id);

    void discoverComments();

    boolean addGlobalComment(Comment comment);

    Integer getNextId(String type);

    boolean modifyUserComment(Integer userId, Integer commentId, String content);

    /* Personal Functions Finish */

}