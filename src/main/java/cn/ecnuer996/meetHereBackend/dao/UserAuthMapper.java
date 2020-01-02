package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.UserAuth;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserAuthMapper {

    int insert(UserAuth record);

    int insertSelective(UserAuth record);

    UserAuth registerJudge(String nickname);

    UserAuth loginJudge(String nickname, String password);

    //此函数寻找nickname登陆方式的验证信息
    UserAuth selectNicknameLoginAuth(Integer id);

    //根据登录方式和对应ID来获取验证信息
    UserAuth selectBySignInMethod(String method,String identifier);

    UserAuth getUserAuthById(int user_id);

    UserAuth getLegalUserAuthById(int user_id);

    UserAuth getIllegalUserAuthById(int user_id);

    boolean forbidUserById(int user_id);

    boolean permitUserById(int user_id);

    UserAuth isForgetBefore(String username);

    ArrayList<UserAuth> getForgetUserAuths();

    void acceptRediscover(String username);

    void refuseRediscover(String username);

}