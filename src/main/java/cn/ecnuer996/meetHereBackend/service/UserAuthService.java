package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.dao.UserAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UserAuthService")
public class UserAuthService {

    @Autowired
    private UserAuthMapper userAuthDao;

    public UserAuth registerJudge(String nickname){
        return userAuthDao.registerJudge(nickname);
    }

    public UserAuth loginJudge(String nickname, String password){
        return userAuthDao.loginJudge(nickname,password);
    }

    public int insert(UserAuth userAuth){
        return userAuthDao.insertSelective(userAuth);
    }

    //目前只采用nickname的登录方式，但数据库设计成可扩展其他登录方式，如phone,wechat,weibo等
    //此函数寻找nickname登陆方式的验证信息
    public UserAuth getNicknameLoginAuth(Integer id){
        return userAuthDao.selectNicknameLoginAuth(id);
    }

    public UserAuth getBySignInMethod(String method,String identifier){
        return userAuthDao.selectBySignInMethod(method,identifier);
    }

    public boolean forbidUserById(int user_id) {
        UserAuth userAuth = userAuthDao.getLegalUserAuthById(user_id);
        if(userAuth == null) return false;
        userAuthDao.forbidUserById(userAuth.getUserId());
        return true;
    }

    public boolean permitUserById(int user_id) {
        UserAuth userAuth = userAuthDao.getIllegalUserAuthById(user_id);
        if(userAuth == null) return false;
        userAuthDao.permitUserById(userAuth.getUserId());
        return true;
    }

    public String getIdentityType(int user_id) {
        UserAuth userAuth = userAuthDao.getUserAuthById(user_id);
        return userAuth.getIdentityType();
    }
}
