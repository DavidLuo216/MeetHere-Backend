package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.User;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.service.ReservationService;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import cn.ecnuer996.meetHereBackend.transfer.TopNVenues;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
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

    @ApiOperation("根据场馆名模糊查询场馆接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "name", value = "场馆名称关键词", required = true) })
    @GetMapping(value="/venue")
    public ArrayList<Venue> getVenues(@RequestParam("name") String name){
        return venueService.getVenueByName("%" + name + "%");
    }

    @ApiOperation("通过venue_id查询某个场馆的所有场地接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "venue_id", value = "场馆id", required = true) })
    @GetMapping(value="/site")
    public ArrayList<Site> getSites(@RequestParam("venue_id")Integer venueId){
        return venueService.getSiteByVenueId(venueId);
    }

    @ApiOperation("分页查询所有场馆信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/venues")
    public JSONObject getAllVenues(@RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page){
        List<VenueInList> pre_venues = venueService.getAllVenues();
        int num_of_pages = Math.max((int) Math.ceil(pre_venues.size() / (double) segment), 1);
        ArrayList<VenueInList> venues = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_venues.size()); ++i){
            venues.add(pre_venues.get(i));
        }
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("messages","查询成功");
        response.put("num_of_pages", num_of_pages);
        response.put("result",venues);
        return response;
    }

    @ApiOperation("最受欢迎的n个场馆信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "n", value = "数目", required = true)})
    @GetMapping(value="/topNVenues")
    public <TopNResult> JSONObject getTopNVenues(@RequestParam("n")Integer n){
        JSONObject response = new JSONObject();
        ArrayList<Integer> siteIds = reservationService.getSiteIdsOfReservations();
        ArrayList<Integer> venueSiteIds = siteService.getVenueSiteIds();
        Map site2venue = new HashMap();
        for(Integer venueSiteId : venueSiteIds){
            site2venue.put(venueSiteId % 10000,venueSiteId / 10000);
        }
        Map venue2times = new HashMap<Integer,Integer>();
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

        ArrayList<TopNVenues> result = new ArrayList<>();

        for(int i = 0; i < n; ++i){
            TopNVenues topNVenues = new TopNVenues();
            topNVenues.rank = i + 1;
            topNVenues.venueId = venueWithTimes.get(i) % 10000;
            topNVenues.times = venueWithTimes.get(i) / 10000;
            result.add(topNVenues
            );
        }

        response.put("code", 200);
        response.put("message", "success");
        response.put("result", result);

        return response;
    }

    @ApiOperation("根据ID查询场馆详情接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "venue_id", value = "场馆id", required = true) })
    @GetMapping(value="/venue-detail")
    public JSONObject getVenueDetail(@RequestParam("venue_id")Integer venueId){
        return venueService.getVenueDetail(venueId);
    }

    @ApiOperation("根据场地ID和日期查询时间段信息接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "site_id", value = "场地id", required = true),
                         @ApiImplicitParam(name = "book_date", value = "日期", required = true)})
    @GetMapping(value="/site-time-list")
    public JSONObject getSiteTimeList(@RequestParam("site_id")Integer siteId,
                                      @RequestParam("book_date")String date){
        JSONObject response=new JSONObject();
        try{
            response.put("date",venueService.getSiteTimes(siteId,date));
            response.put("code",200);
            response.put("message","成功返回此场地预约时段信息");
        }catch(ParseException parseException){
            response.put("code",500);
            response.put("message","日期参数格式不正确");
        }
        return response;
    }

    @ApiOperation("提交预定信息接口")
    @PostMapping(value="/reserve")
    public JSONObject generateReservation(@RequestBody JSONObject postBody){
        int siteId=postBody.getInteger("siteId");
        int userId=postBody.getInteger("userId");
        String date=postBody.getString("bookDate");
        int beginPeriod=postBody.getInteger("beginPeriod");
        int endPeriod=postBody.getInteger("endPeriod");
        JSONObject response=new JSONObject();
        // 验证想预约的时段是否已被预约
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
                        response.put("code",500);
                        response.put("message","您所选的时段已被预约，请重新选择预约时间！");
                        return response;
                    }else{
                        response.put("code",500);
                        response.put("message","您所选的时段处于闭馆时间，请重新选择预约时间！");
                        return response;
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
            response.put("code","200");
            response.put("message","预定成功！");
            ReservationDetail reservationDetail=venueService.getLatestReservation(userId);
            JSONObject reservationDetailJson=new JSONObject();
            reservationDetailJson.put("reservationDetail",reservationDetail);
            response.put("data",reservationDetailJson);
            System.out.println(bookTime.getTime());
        }catch(ParseException pe){
            ;
        }
        return response;
    }

    @ApiOperation("分页查询用户订单列表接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "用户id", required = true),
                         @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/orders")
    public JSONObject searchOrders(@RequestParam("id")Integer userId,
                                   @RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page) {
        JSONObject response = new JSONObject();
        User user = userService.getUserById(userId);
        if(user.getId() < 0){
            response.put("code",250);
            response.put("message","你传了个假用户,拒绝");
        }
        else{
            List<Reservation> pre_reservations = reservationService.getReservationByUserId(userId);
            int num_of_pages = Math.max((int) Math.ceil(pre_reservations.size() / (double) segment), 1);
            List<Reservation> reservations = new ArrayList<>();
            for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_reservations.size()); ++i){
                reservations.add(pre_reservations.get(i));
            }
            response.put("code",200);
            response.put("messages","查询成功");
            response.put("num_of_pages", num_of_pages);
            ArrayList<ReservationDetail> result = new ArrayList<>();
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
                result.add(item);
            }
            response.put("result",result);
        }
        return response;
    }

}
