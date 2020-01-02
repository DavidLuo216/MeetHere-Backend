package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.service.ReservationService;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.transfer.TopNVenues;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
import cn.ecnuer996.meetHereBackend.util.ReservationState;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@Api(tags = "预定相关接口")
public class ReservationController {

    private UserService userService;
    private SiteService siteService;
    private VenueService venueService;
    private ReservationService reservationService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    @Autowired
    public void setVenueService(VenueService venueService) {
        this.venueService = venueService;
    }
    @Autowired
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /* Change Date To String Begin */
    private String DateToDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    private String DateToTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    /* Change Date To String Finish */

    @ApiOperation("通过venue_id查询某个场馆的所有场地接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "venue_id", value = "场馆id", required = true) })
    @GetMapping(value="/site")
    public JsonResult getSites(@RequestParam("venue_id")Integer venueId){
        ArrayList<Site> sites=venueService.getSiteByVenueId(venueId);
        String prefix=FilePathUtil.URL_SITE_IMAGE_PREFIX;
        for (Site s:sites){
            s.setImage(prefix+s.getImage());
        }
        Map<String,Object> result=new HashMap<>(1);
        result.put("sites",sites);
        return new JsonResult(result);
    }

    @ApiOperation("最受欢迎的n个场馆信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "n", value = "数目", required = true)})
    @GetMapping(value="/topNVenues")
    public JsonResult getTopNVenues(@RequestParam("n")Integer n){
        ArrayList<Integer> siteIds = reservationService.getSiteIdsOfReservations();
        ArrayList<Integer> venueSiteIds = siteService.getVenueSiteIds();
        Map site2venue = new HashMap();
        for(Integer venueSiteId : venueSiteIds){
            site2venue.put(venueSiteId % 10000,venueSiteId / 10000);
        }
        Map venue2times = new HashMap<Integer, Integer>();
        for(Integer siteId : siteIds) {
            int venueId = (Integer)site2venue.get(siteId);
            if(venue2times.containsKey(venueId)) {
                venue2times.put(venueId, (Integer)venue2times.get(venueId) + 1);
            }
            else{
                venue2times.put(venueId,1);
            }
        }
        ArrayList<Integer> venueWithTimes = new ArrayList<>();
        for(Object entry : venue2times.entrySet()) {
            venueWithTimes.add((Integer) ((Map.Entry) entry).getKey() + (Integer) ((Map.Entry) entry).getValue() * 10000);
        }
        venueWithTimes.sort(Comparator.reverseOrder());

        n = Math.min(n,venueWithTimes.size());

        ArrayList<TopNVenues> venues = new ArrayList<>();

        for(int i = 0; i < n; ++i){
            TopNVenues topNVenues = new TopNVenues();
            topNVenues.rank = i + 1;
            topNVenues.venueId = venueWithTimes.get(i) % 10000;
            topNVenues.times = venueWithTimes.get(i) / 10000;
            venues.add(topNVenues);
        }

        Map<String,Object> result=new HashMap<>(1);
        result.put("venues",venues);
        return new JsonResult(result);
    }

    @ApiOperation("提交预定信息接口")
    @PostMapping(value="/reserve")
    public JsonResult generateReservation(@RequestBody JSONObject postBody){
        int siteId=postBody.getInteger("siteId");
        int userId=postBody.getInteger("userId");
        String date=postBody.getString("bookDate");
        int beginPeriod=postBody.getInteger("beginPeriod");
        int endPeriod=postBody.getInteger("endPeriod");
        // 验证想预约的时段是否已被预约
        ReservationDetail reservationDetail=null;
        try{
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
            //mysql时区问题尚未解决，只能将日期按照GMT+0时区解析
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date realDate=formatter.parse(date);
            int[] bookList=venueService.getReservationsBySiteIdAndDate(siteId,realDate);
            for(int i=beginPeriod;i<endPeriod;++i){
                //时段不可预约
                if(bookList[i]!=0){
                    if(bookList[i]==1){
                        return new JsonResult(JsonResult.FAIL,"您所选的时段已被预约，请重新选择预约时间！");
                    }else{
                        return new JsonResult(JsonResult.FAIL,"您所选的时段处于闭馆时间，请重新选择预约时间！");
                    }
                }
            }
            Date bookTime=new Date();
            Reservation reservation=new Reservation();
            reservation.setSiteId(siteId);
            reservation.setUserId(userId);
            reservation.setBookTime(bookTime);
            reservation.setDate(realDate);
            reservation.setCost(venueService.calculatePrice(siteId,endPeriod-beginPeriod));
            reservation.setBeginTime(beginPeriod);
            reservation.setEndTime(endPeriod);
            reservation.setState(1);
            venueService.addReservation(reservation);
            reservationDetail=venueService.getLatestReservation(userId);
        }catch(ParseException pe){
            pe.printStackTrace();
            return new JsonResult(JsonResult.FAIL,"时间格式错误");
        }
        Map<String,Object> result=new HashMap<>(1);
        result.put("detail",reservationDetail);
        return new JsonResult(result,"预定成功");
    }

    @ApiOperation("分页查询用户订单列表接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "用户id", required = true),
                         @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/orders")
    public JsonResult searchOrders(@RequestParam("id")Integer userId,
                                   @RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page) {
        User user = userService.getUserById(userId);
        if(user.getId() < 0){
            return new JsonResult(JsonResult.FAIL,"无效的用户ID");
        }
        else{
            List<Reservation> pre_reservations = reservationService.getReservationByUserId(userId);
            int num_of_pages = Math.max((int) Math.ceil(pre_reservations.size() / (double) segment), 1);
            List<Reservation> reservations = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_reservations.size()); ++i){
                reservations.add(pre_reservations.get(i));
            }
            ArrayList<ReservationDetail> details = new ArrayList<>();
            int len = reservations.size();
            for(int i = 0; i < len; i++) {
                Reservation reservation = reservations.get(i);
                ReservationDetail item = new ReservationDetail();
                Site site = siteService.getSiteById(reservation.getSiteId());
                /* Calculate Element Value Begin */
                item.setSiteName(site.getName());
                item.setSiteImage(FilePathUtil.URL_SITE_IMAGE_PREFIX + site.getImage());
                item.setVenueName(venueService.getVenueById(site.getVenueId()).getName());
                item.setBookTime(DateToTime(reservation.getBookTime()));
                item.setReserveDate(DateToDate(reservation.getDate()));
                item.setCost(reservation.getCost());
                item.setBeginTime(venueService.simplePrintPeriod(reservation.getBeginTime()));
                item.setEndTime(venueService.simplePrintPeriod(reservation.getEndTime() + 1));
                item.setState(ReservationState.states.get(reservation.getState()));
                /* Calculate Element Value Finish */
                details.add(item);
            }
            Map<String,Object> result=new HashMap<>(2);
            result.put("details",details);
            result.put("num_of_pages",num_of_pages);
            return new JsonResult(result,"查询成功");
        }
    }

    @ApiOperation("取消订单")
    @PostMapping(value="/cancel")
    public JsonResult cancelReservation(@RequestBody JSONObject postBody){
        int userId=postBody.getInteger("userId");
        int reservationId=postBody.getInteger("reservationId");
        reservationService.cancelReservation(userId, reservationId);
        return new JsonResult("取消成功");
    }

}
