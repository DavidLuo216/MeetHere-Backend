package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    void getSiteTimes() throws ParseException {
        List<Reservation> reservations=new ArrayList<>();
        for (int i=0;i<3;++i){
            Reservation reservation=new Reservation();
            reservation.setBeginTime(16+i*4);
            reservation.setEndTime(19+i*4);
            reservations.add(reservation);
        }
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        when(reservationDao.selectBySiteIdAndDate(1,format.parse("2019-12-12"))).thenReturn(reservations);
        Site site=new Site();
        site.setVenueId(5);
        when(siteDao.selectByPrimaryKey(1)).thenReturn(site);
        Venue venue=new Venue();
        venue.setId(5);
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        venue.setBeginTime(timeFormat.parse("07:00"));
        venue.setEndTime(timeFormat.parse("19:00"));
        when(venueDao.selectByPrimaryKey(5)).thenReturn(venue);
        Map<String,Object> result=venueService.getSiteTimes(1,"2019-12-12");
        List<Map<String,Object>> siteTimes= (List<Map<String, Object>>) result.get("siteTimes");
        assertEquals(siteTimes.get(0).get("bookable"),-1);
        assertEquals(siteTimes.get(13).get("bookable"),-1);
        assertEquals(siteTimes.get(14).get("bookable"),0);
        assertEquals(siteTimes.get(37).get("bookable"),0);
        assertEquals(siteTimes.get(20).get("bookable"),1);
        assertEquals(siteTimes.get(27).get("bookable"),1);
        assertEquals(siteTimes.get(38).get("bookable"),-1);
        assertEquals(siteTimes.get(47).get("bookable"),-1);
    }

    @Test
    void getReservationsBySiteIdAndDate() throws ParseException {
        List<Reservation> reservations=new ArrayList<>();
        for (int i=0;i<3;++i){
            Reservation reservation=new Reservation();
            reservation.setBeginTime(16+i*4);
            reservation.setEndTime(19+i*4);
            reservations.add(reservation);
        }
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        when(reservationDao.selectBySiteIdAndDate(1,format.parse("2019-12-12"))).thenReturn(reservations);
        Site site=new Site();
        site.setVenueId(5);
        when(siteDao.selectByPrimaryKey(1)).thenReturn(site);
        Venue venue=new Venue();
        venue.setId(5);
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        venue.setBeginTime(timeFormat.parse("07:00"));
        venue.setEndTime(timeFormat.parse("19:00"));
        when(venueDao.selectByPrimaryKey(5)).thenReturn(venue);
        int[] bookableList=venueService.getReservationsBySiteIdAndDate(1,format.parse("2019-12-12"));
        assertEquals(bookableList[0],-1);
        assertEquals(bookableList[13],-1);
        assertEquals(bookableList[38],-1);
        assertEquals(bookableList[47],-1);
        assertEquals(bookableList[14],0);
        assertEquals(bookableList[37],0);
        assertEquals(bookableList[20],1);
        assertEquals(bookableList[26],1);
    }

    @Test
    void calculatePrice() {
        Site site=new Site();
        site.setPrice(60f);
        when(siteDao.selectByPrimaryKey(1)).thenReturn(site);
        assertEquals(venueService.calculatePrice(1,4),120f);
    }

    @Test
    void addReservation() {
        when(reservationDao.insertSelective(isA(Reservation.class))).thenReturn(1);
        venueService.addReservation(new Reservation());
        verify(reservationDao,times(1)).insertSelective(isA(Reservation.class));
    }

    @Test
    void getLatestReservation() throws ParseException {
        Reservation reservation=new Reservation();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        reservation.setSiteId(3);
        reservation.setId(12345);
        reservation.setBookTime(new Date());
        reservation.setCost(120f);
        reservation.setDate(format.parse("2019-12-12"));
        reservation.setState(1);
        reservation.setBeginTime(14);
        reservation.setEndTime(16);
        when(reservationDao.selectLatestReservationByUserId(1)).thenReturn(reservation);
        Site site=new Site();
        site.setVenueId(5);
        site.setName("site name");
        Venue venue=new Venue();
        venue.setId(5);
        venue.setName("venue name");
        when(siteDao.selectByPrimaryKey(3)).thenReturn(site);
        when(venueDao.selectByPrimaryKey(5)).thenReturn(venue);
        ReservationDetail reservationDetail=venueService.getLatestReservation(1);
        assertEquals(reservationDetail.getBeginTime(),"07:00");
        assertEquals(reservationDetail.getEndTime(),"08:30");
    }

    @Test
    void simplePrintPeriod(){
        assertEquals(venueService.simplePrintPeriod(18),"09:00");
        assertEquals(venueService.simplePrintPeriod(19),"09:30");
        assertEquals(venueService.simplePrintPeriod(22),"11:00");
        assertEquals(venueService.simplePrintPeriod(23),"11:30");
    }
}