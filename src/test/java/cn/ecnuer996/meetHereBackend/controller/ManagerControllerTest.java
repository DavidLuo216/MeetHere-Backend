package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.ManagerService;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ManagerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ManagerService managerService;
    @MockBean
    private UserAuthService userAuthService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("正常登录")
    void testLogInSuccessfully() throws Exception {
        Manager manager=mock(Manager.class);
        when(manager.getPassword()).thenReturn("rightPassword");
        when(manager.getAvatar()).thenReturn("avatar.jpg");
        when(manager.getId()).thenReturn(1);
        when(manager.getName()).thenReturn("某个管理员");
        when(managerService.getManagerByName("某个管理员")).thenReturn(manager);
        JSONObject requestJson=new JSONObject();
        requestJson.put("name","某个管理员");
        requestJson.put("password","rightPassword");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/manager-sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson.toJSONString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("登录成功"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("不存在的管理员账户登录")
    void testLogInWhenNameDoNotExist() throws Exception {
        when(managerService.getManagerByName(isA(String.class))).thenReturn(null);
        JSONObject requestJson=new JSONObject();
        requestJson.put("name","不存在的管理员账号名");
        requestJson.put("password","anyString");
        mockMvc.perform(MockMvcRequestBuilders
        .post("/manager-sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson.toJSONString()))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("404"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.message")
                .value("不存在的管理员"));
    }

    @Test
    @DisplayName("登录密码错误")
    void testLogInWhenWrongPassword() throws Exception {
        Manager manager=mock(Manager.class);
        when(manager.getPassword()).thenReturn("rightPassword");
        when(managerService.getManagerByName("某个管理员")).thenReturn(manager);
        JSONObject requestJson=new JSONObject();
        requestJson.put("name","某个管理员");
        requestJson.put("password","wrongPassword");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/manager-sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson.toJSONString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("密码错误"));
    }

    @Test
    @DisplayName("查看忘记密码用户空列表")
    void getForgetUsersEmptyList() throws Exception {
        when(userAuthService.getForgetUserAuths()).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/get-forget-users"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

    @Test
    @DisplayName("查看忘记密码用户非空列表")
    void getForgetUsersNotEmptyList() throws Exception {
        ArrayList<UserAuth> userAuths = new ArrayList<>();
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentifier("Ethereality");
        userAuth.setCredential("hahaha");
        userAuths.add(userAuth);
        when(userAuthService.getForgetUserAuths()).thenReturn(userAuths);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/get-forget-users"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("同意找回密码")
    void acceptRediscover() throws Exception {
        when(userAuthService.acceptRediscover(anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/accept-rediscover")
                .param("username", "Ethereality"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

    @Test
    @DisplayName("拒绝找回密码")
    void refuseRediscover() throws Exception {
        when(userAuthService.refuseRediscover(anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/refuse-rediscover")
                .param("username", "Ethereality"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }
}