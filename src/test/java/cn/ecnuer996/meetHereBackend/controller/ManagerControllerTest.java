package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.*;
import cn.ecnuer996.meetHereBackend.service.*;
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
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ManagerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ManagerService managerService;
    @MockBean
    private UserAuthService userAuthService;
    @MockBean
    private VenueService venueService;
    @MockBean
    private ReservationService reservationService;
    @MockBean
    private SiteService siteService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("管理员非空详情")
    void getManager() throws Exception {
        Manager manager = new Manager();
        when(managerService.getManager(anyInt())).thenReturn(manager);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/manager")
                .param("managerId", "1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("管理员空详情")
    void getManagerEmpty() throws Exception {
        when(managerService.getManager(anyInt())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/manager")
                .param("managerId", "1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("404"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
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

    @Test
    @DisplayName("成功查询进行中活动")
    void searchOrders() throws Exception {
        ArrayList<Reservation> reservations=new ArrayList<>();
        for (int i=0;i<10;++i){
            Reservation reservation=new Reservation();
            reservation.setId(i);
            reservation.setSiteId(i);
            reservation.setBookTime(new Date());
            reservation.setDate(new Date());
            reservation.setCost(100f);
            reservation.setBeginTime(i);
            reservation.setEndTime(i+2);
            reservations.add(reservation);
        }
        Site site=new Site();
        site.setName("something");
        site.setVenueId(1);
        Venue venue=new Venue();
        venue.setName("something");
        when(siteService.getSiteById(anyInt())).thenReturn(site);
        when(reservationService.getGoingReservation()).thenReturn(reservations);
        when(venueService.getVenueById(anyInt())).thenReturn(venue);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/going-orders")
                .param("segment","5")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.num_of_pages")
                .value("2"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.details")
                .isNotEmpty());
        verify(venueService,times(5)).getVenueById(anyInt());
        verify(venueService,times(5*2)).simplePrintPeriod(anyInt());
    }

}