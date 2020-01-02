package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.UserAuthMapper;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserAuthServiceTest {

    @Autowired
    UserAuthService userAuthService;

    @MockBean
    UserAuthMapper userAuthDao;

    @Test
    void insert() {
        when(userAuthDao.insertSelective(isA(UserAuth.class))).thenReturn(1);
        userAuthService.insert(new UserAuth());
        verify(userAuthDao,times(1)).insertSelective(isA(UserAuth.class));
    }

    @Test
    void getBySignInMethod() {
        when(userAuthDao.selectBySignInMethod("nickname","用户名")).thenReturn(new UserAuth());
        userAuthService.getBySignInMethod("nickname","用户名");
        verify(userAuthDao,times(1)).selectBySignInMethod("nickname","用户名");
    }

    @Test
    @DisplayName("封禁存在用户")
    void forbidUserById() {
        UserAuth userAuth=new UserAuth();
        userAuth.setUserId(1);
        when(userAuthDao.getLegalUserAuthById(1)).thenReturn(userAuth);
        assertEquals(userAuthService.forbidUserById(1),true);
        verify(userAuthDao,times(1)).getLegalUserAuthById(1);
        verify(userAuthDao,times(1)).forbidUserById(1);
    }

    @Test
    @DisplayName("封禁不存在用户失败")
    void forbidUserByIdWhenUserNotExist() {
        assertEquals(userAuthService.forbidUserById(-1),false);
        verify(userAuthDao,times(1)).getLegalUserAuthById(-1);
    }

    @Test
    void permitUserById() {
        UserAuth userAuth=new UserAuth();
        userAuth.setUserId(1);
        when(userAuthDao.getIllegalUserAuthById(1)).thenReturn(userAuth);
        assertEquals(userAuthService.permitUserById(1),true);
    }

    @Test
    void permitUserByIdWhenUserNotExist() {
        assertEquals(userAuthService.permitUserById(-1),false);
        verify(userAuthDao,times(1)).getIllegalUserAuthById(-1);
    }

    @Test
    void getIdentityType() {
        UserAuth userAuth=new UserAuth();
        userAuth.setIdentityType("nickname");
        when(userAuthDao.getUserAuthById(1)).thenReturn(userAuth);
        assertEquals(userAuthService.getIdentityType(1),"nickname");
    }

    @Test
    void isForgetBefore() {
        when(userAuthDao.isForgetBefore("用户名")).thenReturn(new UserAuth());
        assertEquals(userAuthService.isForgetBefore("用户名"),true);
    }

    @Test
    void getForgetUserAuths() {
        userAuthService.getForgetUserAuths();
        verify(userAuthDao,times(1)).getForgetUserAuths();
    }

    @Test
    void acceptRediscover() {
        userAuthService.acceptRediscover("用户名");
        verify(userAuthDao,times(1)).acceptRediscover("用户名");
    }

    @Test
    void refuseRediscover() {
        userAuthService.refuseRediscover("用户名");
        verify(userAuthDao,times(1)).refuseRediscover("用户名");
    }
}