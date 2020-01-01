package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.service.ReservationService;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private VenueService venueService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SiteService siteService;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @DisplayName("成功获取某个场馆的所有场地")
    void getSites() throws Exception {
        ArrayList<Site> sites=new ArrayList<>();
        for(int i=0;i<5;++i){
            sites.add(new Site());
        }
        when(venueService.getSiteByVenueId(1)).thenReturn(sites);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/site")
        .param("venue_id","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("成功获取场馆排行榜")
    void getTopNVenues() throws Exception {
        ArrayList<Integer> siteIds = new ArrayList<>();
        siteIds.add(1);
        siteIds.add(1);
        ArrayList<Integer> venueSiteIds = new ArrayList<>();
        venueSiteIds.add(10001);
        venueSiteIds.add(20001);

        when(reservationService.getSiteIdsOfReservations()).thenReturn(siteIds);
        when(siteService.getVenueSiteIds()).thenReturn(venueSiteIds);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/topNVenues")
                .param("n","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("失败获取场馆排行榜")
    void getTopNVenuesError() throws Exception {
        ArrayList<Integer> siteIds = new ArrayList<>();
        ArrayList<Integer> venueSiteIds = new ArrayList<>();

        when(reservationService.getSiteIdsOfReservations()).thenReturn(siteIds);
        when(siteService.getVenueSiteIds()).thenReturn(venueSiteIds);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/topNVenues")
                .param("n","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("成功提交预定")
    void generateReservation() throws Exception {
        String content="{\"siteId\":\"1\",\"userId\":\"1\",\"bookDate\":\"2019-12-12\"," +
                "\"beginPeriod\":\"14\",\"endPeriod\":\"17\"}";
        int[] timeList=new int[48];
        for(int i=0;i<48;++i){
            timeList[i]=-1;
        }
        for(int i=14;i<34;++i){
            timeList[i]=0;
        }
        when(venueService.getReservationsBySiteIdAndDate(anyInt(),any())).thenReturn(timeList);
        mockMvc.perform(MockMvcRequestBuilders
        .post("/reserve")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("时间格式错误导致提交预定失败")
    void generateReservationWhenTimeFormatError() throws Exception {
        String content="{\"siteId\":\"1\",\"userId\":\"1\",\"bookDate\":\"2019-14-\"," +
                "\"beginPeriod\":\"14\",\"endPeriod\":\"17\"}";
        int[] timeList=new int[48];
        for(int i=0;i<48;++i){
            timeList[i]=-1;
        }
        for(int i=14;i<34;++i){
            timeList[i]=0;
        }
        when(venueService.getReservationsBySiteIdAndDate(anyInt(),any())).thenReturn(timeList);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/reserve")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("时间格式错误"));

    }

    @Test
    @DisplayName("时段处于闭馆时间导致预定失败")
    void generateReservationWhenBeyondTime() throws Exception {
        String content="{\"siteId\":\"1\",\"userId\":\"1\",\"bookDate\":\"2019-12-12\"," +
                "\"beginPeriod\":\"32\",\"endPeriod\":\"36\"}";
        int[] timeList=new int[48];
        for(int i=0;i<48;++i){
            timeList[i]=-1;
        }
        for(int i=14;i<34;++i){
            timeList[i]=0;
        }
        when(venueService.getReservationsBySiteIdAndDate(anyInt(),any())).thenReturn(timeList);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/reserve")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("您所选的时段处于闭馆时间，请重新选择预约时间！"));
    }

    @Test
    @DisplayName("时段已被预定导致预定失败")
    void generateReservationWhenTimeHasBeenBooked() throws Exception {
        String content="{\"siteId\":\"1\",\"userId\":\"1\",\"bookDate\":\"2019-12-12\"," +
                "\"beginPeriod\":\"19\",\"endPeriod\":\"22\"}";
        int[] timeList=new int[48];
        for(int i=0;i<48;++i){
            timeList[i]=-1;
        }
        for(int i=14;i<34;++i){
            timeList[i]=0;
        }
        for(int i=20;i<30;++i){
            timeList[i]=1;
        }
        when(venueService.getReservationsBySiteIdAndDate(anyInt(),any())).thenReturn(timeList);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/reserve")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("您所选的时段已被预约，请重新选择预约时间！"));
    }

    @Test
    @DisplayName("查询订单失败")
    void searchOrdersError() throws Exception {
        User user = new User();
        user.setId(-1);
        when(userService.getUserById(-1)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders")
                .param("id","-1")
                .param("segment", "2")
                .param("page", "0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isEmpty());
    }

    @Test
    @DisplayName("查询订单成功")
    void searchOrdersSuccess() throws Exception {
        User user = new User();
        user.setId(1);
        ArrayList<Reservation> reservations = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservation.setUserId(1);
        reservation.setSiteId(3);
        reservation.setUserId(1);
        reservation.setDate(new Date());
        reservation.setBookTime(new Date());
        reservation.setCost((float) 10);
        reservation.setBeginTime(17);
        reservation.setEndTime(21);
        reservation.setState(1);
        reservations.add(reservation);
        Site site = new Site();
        site.setId(3);
        site.setName("");
        site.setVenueId(1);
        site.setIntruction("");
        site.setImage("");
        site.setPrice((float)0);
        Venue venue = new Venue();
        when(userService.getUserById(1)).thenReturn(user);
        when(reservationService.getReservationByUserId(1)).thenReturn(reservations);
        when(siteService.getSiteById(anyInt())).thenReturn(site);
        when(venueService.getVenueById(anyInt())).thenReturn(venue);
        when(venueService.simplePrintPeriod(anyInt())).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders")
                .param("id","1")
                .param("segment","2")
                .param("page","0"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }
}