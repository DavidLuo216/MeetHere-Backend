package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.UserAuthMapper;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("UserAuthService")
public class UserAuthService {

    @Autowired
    private UserAuthMapper userAuthDao;

    public int insert(UserAuth userAuth) {
        return userAuthDao.insertSelective(userAuth);
    }

    public UserAuth getBySignInMethod(String method, String identifier) {
        return userAuthDao.selectBySignInMethod(method, identifier);
    }

    public boolean forbidUserById(int user_id) {
        UserAuth userAuth = userAuthDao.getLegalUserAuthById(user_id);
        if (userAuth == null) {
            return false;
        }
        userAuthDao.forbidUserById(userAuth.getUserId());
        return true;
    }

    public boolean permitUserById(int user_id) {
        UserAuth userAuth = userAuthDao.getIllegalUserAuthById(user_id);
        if (userAuth == null) {
            return false;
        }
        userAuthDao.permitUserById(userAuth.getUserId());
        return true;
    }

    public String getIdentityType(int user_id) {
        UserAuth userAuth = userAuthDao.getUserAuthById(user_id);
        return userAuth.getIdentityType();
    }

    public boolean isForgetBefore(String username) {
        UserAuth userAuth = userAuthDao.isForgetBefore(username);
        return userAuth != null;
    }

    public ArrayList<UserAuth> getForgetUserAuths() {
        return userAuthDao.getForgetUserAuths();
    }

    public boolean acceptRediscover(String username) {
        userAuthDao.acceptRediscover(username);
        return true;
    }

    public boolean refuseRediscover(String username) {
        userAuthDao.refuseRediscover(username);
        return true;
    }

}
