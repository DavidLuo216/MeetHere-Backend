package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class VenueServiceTest {

    @Autowired
    VenueService venueService;

    @MockBean
    private VenueMapper venueDao;
    @MockBean
    private SiteMapper siteDao;
    @MockBean
    private VenueImageMapper venueImageDao;
    @MockBean
    private ReservationMapper reservationDao;

    @Test
    void getVenueById() {
        venueService.getVenueById(1);
        verify(venueDao,times(1)).selectByPrimaryKey(1);
    }

    @Test
    void getVenueByName() {
        venueService.getVenueByName("场馆名称");
        verify(venueDao,times(1)).selectByVenueName("场馆名称");
    }

    @Test
    void getSiteByVenueId() {

    }

    @Test
    void getAllVenues() {
    }

    @Test
    void getVenueDetail() {
    }

    @Test
    void getSiteTimes() {
    }

    @Test
    void getReservationsBySiteIdAndDate() {
    }

    @Test
    void calculatePrice() {
    }

    @Test
    void addReservation() {
    }

    @Test
    void getLatestReservation() {
    }
}