package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User selectByNickname(String nickname);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    ArrayList<User> selectAllUsers();

}