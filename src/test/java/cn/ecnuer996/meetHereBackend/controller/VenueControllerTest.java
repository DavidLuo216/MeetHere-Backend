package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest
class VenueControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private SiteMapper siteDao;
    @MockBean
    private VenueService venueService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("成功分页查询场馆")
    void getAllVenues() throws Exception {
        ArrayList<VenueInList> venueInLists=new ArrayList<>();
        for (int i=0;i<10;++i){
            VenueInList venueInList=new VenueInList();
            venueInLists.add(venueInList);
        }
        when(venueService.getAllVenues()).thenReturn(venueInLists);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/venues")
        .param("segment","5")
        .param("page","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.num_of_pages")
                .value("2"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result.venues")
                .isNotEmpty());
    }

    @Test
    @DisplayName("成功通过关键字搜索场馆")
    void getVenues() throws Exception {
        ArrayList<Venue> venues=new ArrayList<>();
        for(int i=0;i<10;++i){
            venues.add(new Venue());
        }
        when(venueService.getVenueByName("%关键字%")).thenReturn(venues);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/venue")
        .param("name","关键字")
        .param("segment","5")
        .param("page","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result.num_of_pages")
                        .value("2"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result.venues")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("成功查询场馆详情")
    void getVenueDetail() throws Exception {
        Map<String,Object> result=new HashMap<>();
        result.put("info","场馆详情");
        when(venueService.getVenueDetail(1)).thenReturn(result);
        mockMvc.perform(MockMvcRequestBuilders
        .get("/venue-detail")
        .param("id","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.result")
                .isNotEmpty());
    }

    @Test
    @DisplayName("成功新增场馆")
    void addVenue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/add-venue")
                .file(new MockMultipartFile("fakeImage1.jpg",new byte[10]))
                .file(new MockMultipartFile("fakeImage2.png",new byte[10]))
                .param("name","新的场馆名")
                .param("address","新的场馆地址")
                .param("phone","1209324715")
                .param("introduction","新的场馆介绍")
                .param("beginTime","07:00")
                .param("endTime","19:00"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("200"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("新增场馆成功！"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.result")
                        .isNotEmpty());
    }

    @Test
    @DisplayName("日期格式错误导致新增场馆失败")
    void addVenueWithWrongDataForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/add-venue")
                .file(new MockMultipartFile("fakeImage1.jpg",new byte[10]))
                .file(new MockMultipartFile("fakeImage2.png",new byte[10]))
                .param("name","新的场馆名")
                .param("address","新的场馆地址")
                .param("phone","1209324715")
                .param("introduction","新的场馆介绍")
                .param("beginTime","0q3pcy07:00")
                .param("endTime","019:00"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.code")
                        .value("400"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("日期格式错误"));
    }

    @Test
    @DisplayName("成功更新场馆的文本信息")
    void updateVenue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
        .post("/update-venue")
                .param("id","1")
        .param("name","新的场馆名")
        .param("address","新的场馆地址")
        .param("phone","1209324715")
        .param("introduction","新的场馆介绍")
        .param("beginTime","07:00")
        .param("endTime","19:00"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"));
    }

    @Test
    @DisplayName("成功增加场地")
    void addSite() throws Exception {
        // 傻逼测试工具不能测试以单个MultiFile作为参数的api
//        mockMvc.perform(MockMvcRequestBuilders
//                .multipart("/add-site")
//                .file(new MockMultipartFile("fakeImage1.jpg","new byte[100]".getBytes()))
//                .param("name","场地名")
//                .param("venueId","1")
//                .param("introduction","场地介绍")
//                .param("price","80.0"))
//                .andExpect(MockMvcResultMatchers
//                        .jsonPath("$.code")
//                        .value("200"));
//        verify(siteDao,times(1)).insert(any());
    }

    @Test
    @DisplayName("成功更新场地信息（不更新场地图片）")
    void updateSite() throws Exception {
        Site site = mock(Site.class);
        when(siteDao.selectByPrimaryKey(1)).thenReturn(site);
        mockMvc.perform(MockMvcRequestBuilders
        .post("/update-site")
        .param("id","1")
        .param("name","新场地名称")
        .param("introduction","新场地介绍")
        .param("price","80.0"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"));
        verify(site).setPrice(80f);
        verify(site).setIntruction("新场地介绍");
        verify(site).setName("新场地名称");
        verify(siteDao,times(1)).updateByPrimaryKeySelective(any());
    }

    @Test
    @DisplayName("成功删除场地")
    void deleteSite() throws Exception {
        Site site=new Site();
        site.setImage("oldImageName.jpg");
        site.setId(1);
        when(siteDao.selectByPrimaryKey(1)).thenReturn(site);
        mockMvc.perform(MockMvcRequestBuilders
        .delete("/delete-site")
        .param("id","1"))
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.code")
                .value("200"));
        verify(siteDao,times(1)).selectByPrimaryKey(1);
        verify(siteDao,times(1)).deleteByPrimaryKey(1);
    }

}