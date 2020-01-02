package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.UserMapper;
import cn.ecnuer996.meetHereBackend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserMapper userDao;

    @Test
    @DisplayName("用ID查询User")
    void getUserById() {
        when(userDao.selectByPrimaryKey(1)).thenReturn(new User());
        userService.getUserById(1);
        verify(userDao,times(1)).selectByPrimaryKey(1);
    }

    @Test
    @DisplayName("用Name查询User")
    void getUserByName() {
        when(userDao.selectByNickname("用户名")).thenReturn(new User());
        userService.getUserByName("用户名");
        verify(userDao,times(1)).selectByNickname("用户名");
    }

    @Test
    @DisplayName("插入User")
    void insert() {
        when(userDao.insertSelective(isA(User.class))).thenReturn(1);
        userService.insert(new User());
        verify(userDao,times(1)).insertSelective(isA(User.class));
    }

    @Test
    @DisplayName("获取所有用户信息")
    void getAllUsers() {
        when(userDao.selectAllUsers()).thenReturn(new ArrayList<>());
        userService.getAllUsers();
        verify(userDao,times(1)).selectAllUsers();
    }

    @Test
    @DisplayName("更新用户信息")
    void update() {
        when(userDao.updateByPrimaryKeySelective(isA(User.class))).thenReturn(1);
        userService.update(new User());
        verify(userDao,times(1)).updateByPrimaryKeySelective(isA(User.class));
    }
}