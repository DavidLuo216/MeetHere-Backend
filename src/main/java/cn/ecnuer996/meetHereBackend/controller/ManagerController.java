package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.service.ManagerService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author LuoChengLing
 */
@CrossOrigin
@RestController
@Api(tags = "管理员相关接口")
public class ManagerController {

    private ManagerService managerService;

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
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
}
