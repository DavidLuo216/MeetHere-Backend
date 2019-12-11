package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service("UserService")
public class UserService {

    @Autowired
    private UserMapper userDao;

    public User getUserById(int id) {
        return userDao.selectByPrimaryKey(id);
    }

    public User getUserByName(String nickname){
        return userDao.selectByNickname(nickname);
    }

    public int insert(User user){
        return userDao.insertSelective(user);
    }

    public ArrayList<User> getAllUsers() {
        return userDao.selectAllUsers();
    }
}
