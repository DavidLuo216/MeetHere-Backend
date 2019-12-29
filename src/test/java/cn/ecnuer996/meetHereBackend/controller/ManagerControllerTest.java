package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.service.ManagerService;
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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ManagerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ManagerService managerService;

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
                .value("500"))
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
}