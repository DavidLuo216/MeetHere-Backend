package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.service.VenueService;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private VenueService venueService;

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
    void getTopNVenues() {
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
    void searchOrders() {
    }
}