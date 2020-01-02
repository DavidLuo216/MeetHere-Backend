package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SiteServiceTest {

    @Autowired
    SiteService siteService;

    @MockBean
    SiteMapper siteDao;

    @Test
    void getSiteById() {
        siteService.getSiteById(1);
        verify(siteDao,times(1)).selectByPrimaryKey(1);
    }

    @Test
    void getVenueSiteIds() {
        siteService.getVenueSiteIds();
        verify(siteDao,times(1)).getVenueSiteIds();
    }

}