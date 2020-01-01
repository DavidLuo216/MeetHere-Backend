package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.util.UploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private UserService userService;
    @MockBean
    private UserAuthService userAuthService;
    @MockBean
    private UploadUtil uploadUtil;


    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("成功登录")
    void signIn() throws Exception {
        UserAuth userAuth=new UserAuth();
        userAuth.setIdentifier("昵称");
        userAuth.setCredential("正确的密码");
        userAuth.setUserId(1);
        User user=new User();
        user.setAvatar("avatarName");
        String content="{\"identifier\":\"昵称\",\"credential\":\"正确的密码\",\"identityType\":\"nickname\"}";
        when(userAuthService.getBySignInMethod("nickname","昵称")).thenReturn(userAuth);
        when(userService.getUserById(1)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
        .post("/sign-in")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
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
    @DisplayName("登录密码错误")
    void signInWithWrongPassword() throws Exception {
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentifier("昵称");
        userAuth.setCredential("正确的密码");
        userAuth.setUserId(1);
        User user = new User();
        user.setAvatar("avatarName");
        String content = "{\"identifier\":\"昵称\",\"credential\":\"错误的密码\",\"identityType\":\"nickname\"}";
        when(userAuthService.getBySignInMethod("nickname", "昵称")).thenReturn(userAuth);
        when(userService.getUserById(1)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/sign-in")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("密码错误！"));
    }

    @Test
    @DisplayName("登录账户不存在")
    void signInWithInvalidUser() throws Exception {
        String content = "{\"identifier\":\"不存在的用户\",\"credential\":\"密码\",\"identityType\":\"nickname\"}";
        mockMvc.perform(MockMvcRequestBuilders
                .post("/sign-in")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("用户不存在！"));
    }

    @Test
    @DisplayName("注册成功")
    void signUp() throws Exception {
        User user=new User();
        user.setId(1);
        String content="{\"email\":\"something@163.com\",\"phone\":\"12309753890\"," +
                "\"nickname\":\"不重复的昵称\",\"password\":\"密码\"}";
        when(userService.getUserByName("不重复的昵称")).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
        .post("/sign-up")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.message")
                .value("注册成功！"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("注册失败用户名已被占用")
    void signUpWithDuplicatedName() throws Exception {
        String content="{\"email\":\"something@163.com\",\"phone\":\"12309753890\"," +
                "\"nickname\":\"重复的昵称\",\"password\":\"密码\"}";
        when(userAuthService.getBySignInMethod("nickname","重复的昵称")).thenReturn(new UserAuth());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/sign-up")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("用户名已被使用！"));
    }

    @Test
    @DisplayName("成功更新用户文本信息")
    void updateUserInfo() throws Exception {
        String content="{\"email\":\"newAddress@163.com\",\"phone\":\"12309753890\"," +
                "\"nickname\":\"新昵称\",\"id\":\"1\"}";
        when(userService.getUserById(1)).thenReturn(new User());
        mockMvc.perform(MockMvcRequestBuilders
        .post("/update-user-info")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.message")
                .value("个人信息更新成功！"));
        verify(userService,times(2)).getUserById(1);
    }

    @Test
    @DisplayName("试图更新不存在用户的文本信息")
    void updateUserInfoWhenUserNotExist() throws Exception {
        String content="{\"email\":\"newAddress@163.com\",\"phone\":\"12309753890\"," +
                "\"nickname\":\"新昵称\",\"id\":\"1\"}";
        mockMvc.perform(MockMvcRequestBuilders
                .post("/update-user-info")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("不允许更新一个不存在的用户的信息！"));
    }

    @Test
    @DisplayName("成功更新用户头像")
    void updateAvatar(){
//        User user=new User();
//        user.setId(1);
//        user.setAvatar("avatarName.jpg");
//        when(userService.getUserById(1)).thenReturn(user);
//        when(uploadUtil.updateFile(isA(MultipartFile.class),anyString(),anyString())).thenReturn(true);
//        mockMvc.perform(MockMvcRequestBuilders
//        .multipart("/update-avatar")
//                .file(new MockMultipartFile("avatar.jpg",new byte[10]))
//        .param("userId","1"))
//                .andExpect(MockMvcResultMatchers
//                .jsonPath("$.code")
//                .value("200"));
//        verify(userService,times(1)).getUserById(1);
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void forbid_user() {
    }

    @Test
    void permit_user() {
    }

    @Test
    void forget_password() {
    }
}