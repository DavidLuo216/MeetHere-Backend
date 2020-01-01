package cn.ecnuer996.meetHereBackend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class VenueControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void addVenue() {
    }

    @Test
    void updateVenue() {
    }

    @Test
    void addSite() throws Exception {
//        File file=new File("F:\\图片\\sea.jpg");
//        String siteName="new Site";
//        FileInputStream fileInputStream=new FileInputStream(file);
//        MockMultipartFile siteImage=new MockMultipartFile("image","sea.jpg",
//                String.valueOf(MediaType.MULTIPART_FORM_DATA),fileInputStream);
//        LinkedMultiValueMap<String,String> params= new LinkedMultiValueMap<>();
//        params.add("venueId","1");
//        params.add("name",siteName);
//        params.add("introduction","info of new site");
//        params.add("price","60.0");
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/add-site")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .params(params)
//        ).andExpect(MockMvcResultMatchers
//                    .jsonPath("$.code")
//                    .value(200))
//                .andExpect(MockMvcResultMatchers
//                    .jsonPath("$.message")
//                    .value("请求成功")
//                );
    }

    @Test
    void updateSite() {
    }

    @Test
    void deleteSite() {
    }
}