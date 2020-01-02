package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.transfer.UserInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
import cn.ecnuer996.meetHereBackend.util.UploadUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @ApiOperation("根据id查用户信息")
    @GetMapping("/get-user-by-id")
    public JsonResult getUserById(@RequestParam int id){
        User user=userService.getUserById(id);
        user.setAvatar(FilePathUtil.URL_USER_AVATAR_PREFIX+user.getAvatar());
        return new JsonResult(user);
    }

    @ApiOperation("用户登录接口")
    @PostMapping(value="/sign-in")
    public JsonResult signIn(@RequestBody UserAuth userAuthParam){
        String identifier=userAuthParam.getIdentifier();
        String credential=userAuthParam.getCredential();
        String signInMethod=userAuthParam.getIdentityType();
        UserAuth userAuth=userAuthService.getBySignInMethod(signInMethod,identifier);
        if(userAuth==null){
            return new JsonResult(JsonResult.NOT_FOUND,"用户不存在！");
        }else if(!userAuth.getCredential().equals(credential)){
            //因为没有邮箱服务和短信服务，
            // 所以邮箱登录方式和短信登录方式的验证码只能静态存储在数据库
            // 简单通过判断字符串是否相等来验证验证码
            String message="";
            if("nickname".equals(signInMethod)){
                message="密码错误！";
            }
            return new JsonResult(JsonResult.FAIL,message);
        }else {
            User user = userService.getUserById(userAuth.getUserId());
            user.setAvatar(FilePathUtil.URL_USER_AVATAR_PREFIX + user.getAvatar());
            //兼容找回密码接口，登陆完拒绝之前所有的找回密码请求
            userAuthService.refuseRediscover(user.getNickname());
            return new JsonResult(user, "登录成功");
        }
    }

    @ApiOperation("用户注册接口")
    @PostMapping(value="/sign-up")
    public JsonResult signUp(@RequestBody JSONObject postBody){
        String email=postBody.getString("email");
        String phone=postBody.getString("phone");
        String nickname=postBody.getString("nickname");
        String password=postBody.getString("password");
        if(userAuthService.getBySignInMethod("nickname",nickname)!=null){
            return new JsonResult(JsonResult.FAIL,"用户名已被使用！");
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

            int user_id=userService.getUserByName(nickname).getId();

            nicknameAuth.setUserId(user_id);
            nicknameAuth.setIdentityType("nickname");
            nicknameAuth.setIdentifier(nickname);
            nicknameAuth.setCredential(password);
            userAuthService.insert(nicknameAuth);
            return new JsonResult(user,"注册成功！");
        }
    }

    @ApiOperation("更新用户文本信息接口")
    @PostMapping(value="/update-user-info")
    public JsonResult updateUserInfo(@RequestBody User user){
        if(user.getId()==null || userService.getUserById(user.getId())==null){
            return new JsonResult(JsonResult.FAIL,"不允许更新一个不存在的用户的信息！");
        }else{
            //不对头像进行更新
            user.setAvatar(null);
            userService.update(user);
            return new JsonResult(userService.getUserById(user.getId()),"个人信息更新成功！");
        }
    }

    @ApiOperation("更新用户头像")
    @PostMapping(value = "/update-avatar")
    public JsonResult updateAvatar(@RequestParam("avatar") MultipartFile avatar,
                                   @RequestParam("userId") int userId){
        User user=userService.getUserById(userId);
        UploadUtil uploadUtil=new UploadUtil();
        if (user==null){
            return new JsonResult(JsonResult.FAIL,"不存在的用户！");
        }else{
            String avatarName=avatar.getOriginalFilename();
            //获取图片格式后缀
            String suffix = avatarName.substring(avatarName.lastIndexOf("."));
            boolean uploadResult;
            // 通过UUID生成唯一的图片名
            String newAvatarName=UUID.randomUUID()+suffix;
            String fileToSave=FilePathUtil.LOCAL_USER_AVATAR_PREFIX+ newAvatarName;
            //用户头像不为默认头像，需要删除旧头像，即更新头像文件
            if(!"default.jpg".equals(user.getAvatar())){
                String fileToDelete=FilePathUtil.LOCAL_USER_AVATAR_PREFIX+user.getAvatar();
                uploadResult=uploadUtil.updateFile(avatar,fileToSave,fileToDelete);
            }else{
                //不需要删除默认头像
                uploadResult=uploadUtil.upload(avatar,fileToSave);
            }
            if(uploadResult){
                //更新图片成功
                user.setAvatar(newAvatarName);
                userService.update(user);
                return new JsonResult(user,"更新头像成功！");
            }else{
                return new JsonResult(JsonResult.FAIL,"更新头像失败！");
            }
        }
    }

    @ApiOperation("分页查询所有用户信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/users")
    public JsonResult getAllUsers(@RequestParam("segment")Integer segment,
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
        Map<String,Object> result=new HashMap<>(2);
        result.put("num_of_pages",num_of_pages);
        result.put("users",users);
        return new JsonResult(result,"查询成功");
    }

    @ApiOperation("用户封号")
    @ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true)})
    @GetMapping(value="/forbid-user")
    public JsonResult forbid_user(@RequestParam("user_id")Integer user_id) {
        boolean result = userAuthService.forbidUserById(user_id);
        if(result){
            return new JsonResult("封号成功");
        }
        else{
            return new JsonResult(JsonResult.NOT_FOUND,"用户不存在");
        }
    }

    @ApiOperation("用户解封")
    @ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true)})
    @GetMapping(value="/permit-user")
    public JsonResult permit_user(@RequestParam("user_id")Integer user_id) {
        boolean result = userAuthService.permitUserById(user_id);
        if(result){
            return new JsonResult("解封成功");
        }
        else{
            return new JsonResult(JsonResult.FAIL,"用户未被封号");
        }
    }

    @ApiOperation("忘记密码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    @GetMapping(value="/forget-password")
    public JsonResult forget_password(@RequestParam("username")String username) {
        UserAuth userAuth = new UserAuth("rediscover",username,"19260817");
        if(userAuthService.isForgetBefore(username)) {
            return new JsonResult(JsonResult.FAIL,"???");
        }
        else{
            userAuthService.insert(userAuth);
            return new JsonResult("记录成功");
        }
    }

}
