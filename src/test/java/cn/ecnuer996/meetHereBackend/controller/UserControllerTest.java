package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.UserAuth;
import cn.ecnuer996.meetHereBackend.service.UserAuthService;
import cn.ecnuer996.meetHereBackend.service.UserService;
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

import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;
    @MockBean
    private UserAuthService userAuthService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("根据ID成功查询用户信息")
    void getUserById() throws Exception {
        User user=new User();
        user.setId(1);
        user.setAvatar("imageName.jpg");
        when(userService.getUserById(1)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/get-user-by-id")
        .param("id","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
        verify(userService,times(1)).getUserById(1);
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
    @DisplayName("成功分页查询所有用户信息")
    void getAllUsers() throws Exception {
        ArrayList<User> users=new ArrayList<>();
        for (int i=0;i<10;++i){
            User user=new User();
            user.setId(i);
            users.add(user);
        }
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/users")
        .param("segment","5")
        .param("page","0"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.num_of_pages")
                .value("2"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.users")
                .isNotEmpty());
        verify(userAuthService,times(1)).getIdentityType(0);
        verify(userAuthService,times(1)).getIdentityType(4);
        verify(userAuthService,times(5)).getIdentityType(anyInt());
    }

    @Test
    @DisplayName("成功封禁用户")
    void forbid_user() throws Exception {
        when(userAuthService.forbidUserById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/forbid-user")
        .param("user_id","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.message")
                .value("封号成功"));
        verify(userAuthService,times(1)).forbidUserById(1);
    }

    @Test
    @DisplayName("试图封禁不存在用户而失败")
    void forbid_userWhenUserNotExist() throws Exception {
        when(userAuthService.forbidUserById(-1)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/forbid-user")
                .param("user_id","-1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("用户不存在"));
        verify(userAuthService,times(1)).forbidUserById(-1);
    }

    @Test
    @DisplayName("成功解封用户")
    void permit_user() throws Exception {
        when(userAuthService.permitUserById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/permit-user")
                .param("user_id","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("解封成功"));
        verify(userAuthService,times(1)).permitUserById(1);
    }

    @Test
    @DisplayName("试图解封不存在或未被封禁的用户")
    void permit_userWhenUserIsNotForbidden() throws Exception {
        when(userAuthService.permitUserById(-1)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/permit-user")
                .param("user_id","-1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("用户未被封号"));
        verify(userAuthService,times(1)).permitUserById(-1);
    }

    @Test
    @DisplayName("忘记密码成功")
    void forgetPasswordSuccess() throws Exception {
        when(userAuthService.isForgetBefore(anyString())).thenReturn(false);
        when(userAuthService.insert(any())).thenReturn(0);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/forget-password")
                .param("username","Ethereality"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("忘记密码失败")
    void forgetPasswordFail() throws Exception {
        when(userAuthService.isForgetBefore(anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/forget-password")
                .param("username","Ethereality"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

}