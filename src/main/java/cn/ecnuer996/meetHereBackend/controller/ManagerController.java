package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.ManagerService;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author LuoChengLing
 */
@CrossOrigin
@RestController
@Api(tags = "管理员相关接口")
public class ManagerController {

    private UserService userService;

    private ManagerService managerService;

    private UserAuthService userAuthService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Autowired
    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @ApiOperation("管理员登录接口")
    @PostMapping("/manager-sign-in")
    public JSONObject logIn(@RequestBody JSONObject request){
        JSONObject response=new JSONObject();
        String name=request.getString("name");
        String password=request.getString("password");
        Manager manager=managerService.getManagerByName(name);
        if(manager==null){
            response.put("code",500);
            response.put("message","不存在的管理员");
        }else if(!manager.getPassword().equals(password)){
            response.put("code",400);
            response.put("message","密码错误");
        }else{
            JSONObject result=new JSONObject();
            result.put("name",manager.getName());
            result.put("avatar", FilePathUtil.URL_MANAGER_AVATAR_PREFIX+manager.getAvatar());
            result.put("id",manager.getId());
            response.put("result",result);
            response.put("code",200);
            response.put("message","登录成功");
        }
        return response;
    }

    @ApiOperation("获取申请找回密码的用户接口")
    @GetMapping("/get-forget-users")
    public JSONObject getForgetUsers() {
        JSONObject response=new JSONObject();
        ArrayList<UserAuth> userAuths = userAuthService.getForgetUserAuths();
        ArrayList<User> users = new ArrayList<>();
        for(UserAuth userAuth : userAuths) {
            users.add(userService.getUserByName(userAuth.getIdentifier()));
        }
        if(users.size() == 0) {
            response.put("code", 404);
            response.put("message", "列表为空");
            response.put("result", null);
        }
        else {
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("result", users);
        }
        return response;
    }

    @ApiOperation("同意找回密码接口")
    @GetMapping("/accept-rediscover")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JSONObject acceptRediscover(
        @RequestParam("username")String username) {
        JSONObject response=new JSONObject();
        userAuthService.acceptRediscover(username);
        response.put("code", 200);
        response.put("message", "修改成功");
        return response;
    }

    @ApiOperation("拒绝找回密码接口")
    @GetMapping("/refuse-rediscover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JSONObject refuseRediscover(
            @RequestParam("username")String username) {
        JSONObject response=new JSONObject();
        userAuthService.refuseRediscover(username);
        response.put("code", 200);
        response.put("message", "拒绝成功");
        return response;
    }

}
