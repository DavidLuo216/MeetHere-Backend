package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.ReservationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("VenueService")
public class VenueService {

    private VenueMapper venueDao;

    private SiteMapper siteDao;

    private VenueImageMapper venueImageDao;

    private ReservationMapper reservationDao;

    @Autowired
    public void setVenueDao(VenueMapper venueDao) {
        this.venueDao = venueDao;
    }
    @Autowired
    public void setSiteDao(SiteMapper siteDao) {
        this.siteDao = siteDao;
    }
    @Autowired
    public void setVenueImageDao(VenueImageMapper venueImageDao) {
        this.venueImageDao = venueImageDao;
    }
    @Autowired
    public void setReservationDao(ReservationMapper reservationDao) {
        this.reservationDao = reservationDao;
    }

    /**
     * 预定的最小时间单元，单位为分钟
     */
    final static int ReservationPeriod=30;

    public Venue getVenueById(int id) {
        return venueDao.selectByPrimaryKey(id);
    }

    public ArrayList<Venue> getVenueByName(String name){
        return venueDao.selectByVenueName(name);
    }

    public ArrayList<Site> getSiteByVenueId(int venue_id){
        ArrayList<Site> sites=siteDao.selectByVenueId(venue_id);
        for(Site site:sites){
            site.setImage(FilePathUtil.URL_SITE_IMAGE_PREFIX +site.getImage());
        }
        return sites;
    }

    public ArrayList<VenueInList> getAllVenues(){
        List<Venue> rawVenues=venueDao.selectAllVenues();
        List<VenueInList> listVenues=new ArrayList<>();
        SimpleDateFormat formatter=new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        for(Venue venue:rawVenues){
            VenueInList venueItem=new VenueInList();
            venueItem.id=venue.getId();
            venueItem.name=venue.getName();
            venueItem.address=venue.getAddress();
            venueItem.beginTime=venue.getBeginTime();
            venueItem.endTime=venue.getEndTime();
            venueItem.cover=FilePathUtil.URL_VENUE_COVER_PREFIX+venueImageDao.getVenueCoverByVenueId(venue.getId());
            listVenues.add(venueItem);
        }
        return (ArrayList<VenueInList>) listVenues;
    }

    public Map<String, Object> getVenueDetail(int venue_id){
        Venue venue=venueDao.selectByPrimaryKey(venue_id);
        List<String> images=venueImageDao.getVenueImagesByVenueId(venue_id);
        for(int i=0;i<images.size();++i){
            images.set(i,FilePathUtil.URL_VENUE_COVER_PREFIX+images.get(i));
        }
        Map<String,Object> venueDetail=new HashMap<>(9);
        SimpleDateFormat formatter=new SimpleDateFormat("HH:mm");
        venueDetail.put("id",venue.getId());
        venueDetail.put("name",venue.getName());
        venueDetail.put("address",venue.getAddress());
        venueDetail.put("introduction",venue.getIntroduction());
        venueDetail.put("phone",venue.getPhone());
        venueDetail.put("beginTime",formatter.format(venue.getBeginTime()));
        venueDetail.put("endTime",formatter.format(venue.getEndTime()));
        venueDetail.put("images",images);
        ArrayList sites=getSiteByVenueId(venue_id);
        venueDetail.put("sites",sites);
        return venueDetail;
    }

    public Map<String, Object> getSiteTimes(int siteId, String date) throws ParseException {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        //mysql时区问题尚未解决，只能将日期按照GMT+0时区解析
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date realDate=formatter.parse(date);
        List<Reservation> reservations=reservationDao.selectBySiteIdAndDate(siteId,realDate);

        Site site=siteDao.selectByPrimaryKey(siteId);
        Venue venue=venueDao.selectByPrimaryKey(site.getVenueId());

        int beginMinutes=(int)((((venue.getBeginTime().getTime())/60000)%1440));
        int endMinutes=(int)((((venue.getEndTime().getTime())/60000)%1440));
        int beginId=beginMinutes/ReservationPeriod;
        int endId=endMinutes/ReservationPeriod;

        int[] bookableList=new int[48];
        Arrays.fill(bookableList, -1);
        for(int i=beginId;i<endId;++i){
            bookableList[i]=0;
        }
        for(Reservation r:reservations){
            for(int i=r.getBeginTime();i<=r.getEndTime();++i){
                // 标记此时段已被预约
                bookableList[i]=1;
            }
        }
        Map<String,Object> ret=new HashMap<>(2);
        List<Map<String,Object>> siteTimes=new ArrayList<>();
        for(int i=0;i<bookableList.length;++i){
            Map<String,Object> siteTime=new HashMap<>(3);
            siteTime.put("period",simplePrintPeriod(i));
            siteTime.put("bookable",bookableList[i]);
            siteTime.put("periodId",i);
            siteTimes.add(siteTime);
        }
        ret.put("siteTimes",siteTimes);
        ret.put("bookDate",date);
        return ret;
    }

    /**
     * 根据要预定的场地ID和日期返回一个布尔值列表表示各时段是否可预约
     * @param siteId
     * @param date
     * @return
     */
    public int[] getReservationsBySiteIdAndDate(int siteId,Date date){
        List<Reservation> reservations=reservationDao.selectBySiteIdAndDate(siteId,date);
        Site site=siteDao.selectByPrimaryKey(siteId);
        Venue venue=venueDao.selectByPrimaryKey(site.getVenueId());

        int beginMinutes=(int)((((venue.getBeginTime().getTime())/60000)%1440));
        int endMinutes=(int)((((venue.getEndTime().getTime())/60000)%1440));
        int beginId=beginMinutes/ReservationPeriod;
        int endId=endMinutes/ReservationPeriod;

        int[] bookableList=new int[48];
        for(int i=0;i<bookableList.length;++i){
            bookableList[i]=-1;
        }
        for(int i=beginId;i<endId;++i){
            bookableList[i]=0;
        }
        for(Reservation r:reservations){
            for(int i=r.getBeginTime();i<r.getEndTime();++i){
                // 标记此时段已被预约
                bookableList[i]=1;
            }
        }
        return bookableList;
    }

    public Float calculatePrice(int siteId,int periodNum){
        return siteDao.selectByPrimaryKey(siteId).getPrice()*periodNum/2;
    }

    public int addReservation(Reservation reservation){
        return reservationDao.insertSelective(reservation);
    }

    public ReservationDetail getLatestReservation(int userId){
        ReservationDetail reservationDetail=new ReservationDetail();
        Reservation latestReservation=reservationDao.selectLatestReservationByUserId(userId);
        SimpleDateFormat dateTimeFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //mysql时区问题尚未解决，只能将日期按照GMT+8时区解析
        dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        Site site=siteDao.selectByPrimaryKey(latestReservation.getSiteId());
        Venue venue=venueDao.selectByPrimaryKey(site.getVenueId());

        reservationDetail.setId(latestReservation.getId());
        reservationDetail.setVenueName(venue.getName());
        reservationDetail.setSiteName(site.getName());
        reservationDetail.setSiteImage(FilePathUtil.URL_SITE_IMAGE_PREFIX+site.getImage());
        reservationDetail.setBookTime(dateTimeFormatter.format(latestReservation.getBookTime()));
        reservationDetail.setCost(latestReservation.getCost());
        reservationDetail.setReserveDate(dateFormatter.format(latestReservation.getDate()));
        reservationDetail.setState(ReservationState.states.get(latestReservation.getSiteId()));

        reservationDetail.setBeginTime(simplePrintPeriod(latestReservation.getBeginTime()));
        reservationDetail.setEndTime(simplePrintPeriod(latestReservation.getEndTime()+1));

        return reservationDetail;
    }

    public String simplePrintPeriod(int periodId){
        if(periodId%2==0){
            if(periodId/2<10) {
                return "0"+(periodId/2)+":00";
            } else {
                return (periodId/2)+":00";
            }
        }else{
            if(periodId/2<10) {
                return "0"+(periodId/2)+":30";
            } else {
                return (periodId/2)+":30";
            }
        }
    }

}


