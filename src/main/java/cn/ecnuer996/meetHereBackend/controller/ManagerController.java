package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.ManagerService;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @ApiOperation("管理员详情")
    @GetMapping("/manager")
    @ApiImplicitParams({@ApiImplicitParam(name = "managerId",value = "管理员ID", required = true)})
    public JsonResult getManagerDetail(@RequestParam int managerId) {
        Manager manager = managerService.getManager(managerId);
        if(manager == null) {
            return new JsonResult(JsonResult.NOT_FOUND, "管理员不存在");
        }
        else {
            Map<String, Object> result=new HashMap<>(1);
            result.put("manager", manager);
            return new JsonResult(result);
        }
    }

    @ApiOperation("管理员登录接口")
    @PostMapping("/manager-sign-in")
    public JsonResult logIn(@RequestBody Manager managerParam){
        String name=managerParam.getName();
        String password=managerParam.getPassword();
        Manager manager=managerService.getManagerByName(name);
        if(manager==null){
            return new JsonResult(JsonResult.NOT_FOUND,"不存在的管理员");
        }
        else if(!manager.getPassword().equals(password)){
            return new JsonResult(JsonResult.FAIL,"密码错误");
        }
        else{
            Map<String,Object> result=new HashMap<>(3);
            result.put("name",manager.getName());
            result.put("avatar", FilePathUtil.URL_MANAGER_AVATAR_PREFIX+manager.getAvatar());
            result.put("id",manager.getId());
            return new JsonResult(result,"登录成功");
        }
    }

    @ApiOperation("获取申请找回密码的用户接口")
    @GetMapping("/get-forget-users")
    public JsonResult getForgetUsers() {
        ArrayList<UserAuth> userAuths = userAuthService.getForgetUserAuths();
        ArrayList<User> users = new ArrayList<>();
        for(UserAuth userAuth : userAuths) {
            users.add(userService.getUserByName(userAuth.getIdentifier()));
        }
        if(users.size() == 0) {
            return new JsonResult(JsonResult.NOT_FOUND,"列表为空");
        }
        else {
            return new JsonResult(users);
        }
    }

    @ApiOperation("同意找回密码接口")
    @GetMapping("/accept-rediscover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JsonResult acceptRediscover(
            @RequestParam("username")String username) {
        userAuthService.acceptRediscover(username);
        return new JsonResult();
    }

    @ApiOperation("拒绝找回密码接口")
    @GetMapping("/refuse-rediscover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JsonResult refuseRediscover(
            @RequestParam("username")String username) {
        userAuthService.refuseRediscover(username);
        return new JsonResult();
    }

}

