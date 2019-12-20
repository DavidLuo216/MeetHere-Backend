package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.transfer.UserInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin
@RestController
@Api(tags = "用户相关接口")
public class UserController {
    private UserService userService;
    private UserAuthService userAuthService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @ApiOperation("用户登录接口")
    @PostMapping(value="/sign-in")
    public @ResponseBody JSONObject signIn(@RequestBody JSONObject postBody){
        String identifier=postBody.getString("id");
        String credential=postBody.getString("credential");
        String signInMethod=postBody.getString("method");
        JSONObject response=new JSONObject();
        UserAuth userAuth=userAuthService.getBySignInMethod(signInMethod,identifier);
        if(userAuth==null){
            response.put("code",500);
            response.put("message","用户不存在！");
            return response;
        }else if(!userAuth.getCredential().equals(credential)){
            //因为没有邮箱服务和短信服务，
            // 所以邮箱登录方式和短信登录方式的验证码只能静态存储在数据库
            // 简单通过判断字符串是否相等来验证验证码
            response.put("code",400);
            if(signInMethod.equals("nickname")){
                response.put("message","密码错误！");
            }else if(signInMethod.equals("phone")){
                response.put("message","手机验证码错误！");
            }else if(signInMethod.equals("email")){
                response.put("message","邮箱验证码错误！");
            }
            return response;
        }else{
            response.put("code",200);
            response.put("message","登录成功");
            User user=userService.getUserById(userAuth.getUserId());
            user.setAvatar(FilePathUtil.URL_USER_AVATAR_PREFIX +user.getAvatar());
            response.put("data",user);
            return response;
        }
    }

    @ApiOperation("用户注册接口")
    @PostMapping(value="/sign-up")
    public @ResponseBody JSONObject signUp(@RequestBody JSONObject postBody){
        JSONObject response=new JSONObject();
        String email=postBody.getString("email");
        String phone=postBody.getString("phone");
        String nickname=postBody.getString("nickname");
        String password=postBody.getString("password");
        if(userAuthService.getBySignInMethod("nickname",nickname)!=null){
            response.put("code",400);
            response.put("message","用户名已被使用！");
            return response;
        }else if(userAuthService.getBySignInMethod("phone",phone)!=null){
            response.put("code",400);
            response.put("message","手机号已被注册！");
            return response;
        }else if(userAuthService.getBySignInMethod("email",email)!=null){
            response.put("code",400);
            response.put("message","邮箱已被注册！");
            return response;
        }else{
            User user=new User();
            user.setId(null);
            user.setNickname(nickname);
            // 默认头像
            user.setAvatar("default.jpg");
            user.setEmail(email);
            user.setPhone(phone);
            userService.insert(user);
            user.setAvatar(FilePathUtil.URL_USER_AVATAR_PREFIX+user.getAvatar());

            UserAuth nicknameAuth=new UserAuth();
            UserAuth phoneAuth=new UserAuth();
            UserAuth emailAuth=new UserAuth();

            int user_id=userService.getUserByName(nickname).getId();

            nicknameAuth.setUserId(user_id);
            nicknameAuth.setIdentityType("nickname");
            nicknameAuth.setIdentifier(nickname);
            nicknameAuth.setCredential(password);
            userAuthService.insert(nicknameAuth);

            response.put("code",200);
            response.put("message","注册成功！");
            response.put("data",user);
            return response;
        }
    }

    @ApiOperation("更新用户信息接口")
    @PostMapping(value="/update-user-info")
    public @ResponseBody JSONObject updateUserInfo(@RequestBody User user){
        JSONObject response=new JSONObject();
        if(user.getId()==null || userService.getUserById(user.getId())==null){
            response.put("code",400);
            response.put("message","不允许更新一个不存在的用户的信息！");
            return response;
        }else{
            userService.insert(user);
            response.put("code",200);
            response.put("message","个人信息更新成功！");
            response.put("data",userService.getUserById(user.getId()));
            return response;
        }
    }

    @ApiOperation("分页查询所有用户信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/users")
    public JSONObject getAllUsers(@RequestParam("segment")Integer segment,
                                  @RequestParam("page")Integer page){
        ArrayList<User> pre_users = userService.getAllUsers();
        int num_of_pages = Math.max((int) Math.ceil(pre_users.size() / (double) segment), 1);
        ArrayList<UserInList> users = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_users.size()); ++i){
            UserInList userInList = new UserInList();
            User pre_user = pre_users.get(i);
            /* Calculate Element Value Begin */
            userInList.user_id = pre_user.getId();
            userInList.nickname = pre_user.getNickname();
            userInList.phone = pre_user.getPhone();
            userInList.email = pre_user.getEmail();
            userInList.identity_type = userAuthService.getIdentityType(userInList.user_id);
            /* Calculate Element Value Finish */
            users.add(userInList);
        }
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("messages","查询成功");
        response.put("num_of_pages", num_of_pages);
        response.put("result",users);
        return response;
    }

    @ApiOperation("用户封号")
    @ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true)})
    @GetMapping(value="/forbid-user")
    public JSONObject forbid_user(@RequestParam("user_id")Integer user_id) {
        JSONObject response = new JSONObject();
        boolean result = userAuthService.forbidUserById(user_id);
        if(result){
            response.put("code",200);
            response.put("messages","封号成功");
        }
        else{
            response.put("code",404);
            response.put("messages","用户不存在");
        }
        return response;
    }

    @ApiOperation("用户解封")
    @ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true)})
    @GetMapping(value="/permit-user")
    public JSONObject permit_user(@RequestParam("user_id")Integer user_id) {
        JSONObject response = new JSONObject();
        boolean result = userAuthService.permitUserById(user_id);
        if(result){
            response.put("code",200);
            response.put("messages","解封成功");
        }
        else{
            response.put("code",404);
            response.put("messages","用户未被封号");
        }
        return response;
    }

    @ApiOperation("忘记密码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    @GetMapping(value="/forget-password")
    public JSONObject forget_password(@RequestParam("username")String username) {
        JSONObject response = new JSONObject();
        UserAuth userAuth = new UserAuth("rediscover",username,"19260817");
        if(userAuthService.isForgetBefore(username)) {
            response.put("code", 304);
            response.put("message", "记录成功");
        }
        else{
            userAuthService.insert(userAuth);
            response.put("code", 200);
            response.put("message", "记录成功");
        }
        return response;
    }

}
