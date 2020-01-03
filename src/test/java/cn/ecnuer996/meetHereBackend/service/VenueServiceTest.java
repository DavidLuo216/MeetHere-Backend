package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        ArrayList<Site> sites=new ArrayList<>();
        for(int i=0;i<5;++i){
            Site site=new Site();
            site.setImage("image.jpg");
            sites.add(site);
        }
        when(siteDao.selectByVenueId(1)).thenReturn(sites);
        ArrayList<Site> result=venueService.getSiteByVenueId(1);
        assertEquals(result.size(),5);
        assertEquals(result.get(0).getImage(), FilePathUtil.URL_SITE_IMAGE_PREFIX+"image.jpg");
    }

    @Test
    void getAllVenues() throws ParseException {
        List<Venue> venues=new ArrayList<>();
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        for(int i=0;i<5;++i){
            Venue venue=new Venue();
            venue.setId(i);
            venue.setName("venue name");
            venue.setAddress("venue address");
            venue.setBeginTime(format.parse("07:00"));
            venue.setBeginTime(format.parse("19:00"));
            venues.add(venue);
        }
        when(venueDao.selectAllVenues()).thenReturn(venues);
        when(venueImageDao.getVenueCoverByVenueId(anyInt())).thenReturn("image.jpg");
        ArrayList<VenueInList> venueInLists=venueService.getAllVenues();
        assertEquals(venueInLists.size(),5);
        verify(venueDao,times(1)).selectAllVenues();
        verify(venueImageDao,times(1)).getVenueCoverByVenueId(0);
        verify(venueImageDao,times(1)).getVenueCoverByVenueId(4);
        verify(venueImageDao,times(5)).getVenueCoverByVenueId(anyInt());
    }

    @Test
    void getVenueDetail() throws ParseException {
        Venue venue=new Venue();
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        venue.setId(1);
        venue.setName("venue name");
        venue.setAddress("venue address");
        venue.setIntroduction("venue introduction");
        venue.setPhone("10982398478");
        venue.setBeginTime(format.parse("07:00"));
        venue.setEndTime(format.parse("19:00"));
        when(venueDao.selectByPrimaryKey(1)).thenReturn(venue);
        List<String> images=new ArrayList<>();
        for(int i=0;i<5;++i){
            images.add("image"+i+".jpg");
        }
        when(venueImageDao.getVenueImagesByVenueId(1)).thenReturn(images);
        Map<String,Object> result=venueService.getVenueDetail(1);
        assertEquals(result.get("beginTime"),"07:00");
        assertEquals(result.get("endTime"),"19:00");
        assertEquals(((List<String>)result.get("images")).size(),5);
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