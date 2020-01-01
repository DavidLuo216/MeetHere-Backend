package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class SiteControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private SiteService siteService;
    @MockBean
    private VenueService venueService;


    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("成功获取场地详情")
    void getSiteDetail() throws Exception {
        Site site=new Site();
        site.setImage("imageName");
        when(siteService.getSiteById(1)).thenReturn(site);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/site-detail")
                .param("id","1"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("试图获取不存在的场地详情")
    void getSiteDetailWhenSiteNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
        .get("/site-detail")
        .param("id","-1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("404"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.message")
                .value("场地不存在"));
    }

    @Test
    @DisplayName("成功查询场地可预订时段")
    void getSiteTimeList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/site-time-list")
                .param("id","1")
                .param("date","2019-12-12"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("时间格式错误导致查询时段失败")
    void getSiteTimeListWhenTimeFormatError() throws Exception {
        when(venueService.getSiteTimes(1,"2019-12-")).thenThrow(new ParseException("日期格式错误",1));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/site-time-list")
                .param("id","1")
                .param("date","2019-12-"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("日期参数格式不正确"));
    }

}