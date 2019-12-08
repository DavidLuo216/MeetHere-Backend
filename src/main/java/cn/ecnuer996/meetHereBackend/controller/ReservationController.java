package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.*;
import cn.ecnuer996.meetHereBackend.service.ReservationService;
import cn.ecnuer996.meetHereBackend.service.UserService;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.util.ReservationState;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@Api(tags = "预定相关接口")
public class ReservationController {

    @Autowired
    private UserService userService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private VenueService venueService;
    @Autowired
    private ReservationService reservationService;

    String urlPrefix="https://ecnuer996.cn/images";

    /* Change Date To String Begin */
    private String DateToDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    private String DateToTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    /* Change Date To String Finish */

    @ApiOperation("根据场馆名模糊查询场馆接口")
    @GetMapping(value="/venue")
    public ArrayList<Venue> getVenues(@RequestParam("name") String name){
        return venueService.getVenueByName("%" + name + "%");
    }

    @ApiOperation("通过venue_id查询某个场馆的所有场地接口")
    @GetMapping(value="/site")
    public ArrayList<Site> getSites(@RequestParam("venue_id")Integer venueId){
        return venueService.getSiteByVenueId(venueId);
    }

    @ApiOperation("查询所有场馆信息接口")
    @GetMapping(value="all-venues")
    public JSONObject getAllVenues(){
        return venueService.getAllVenues();
    }

    @ApiOperation("根据ID查询场馆详情接口")
    @GetMapping(value="venue-detail")
    public JSONObject getVenueDetail(@RequestParam("venue_id")Integer venueId){
        return venueService.getVenueDetail(venueId);
    }

    @ApiOperation("根据场地ID和日期查询时间段信息接口")
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
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0")); //mysql时区问题尚未解决，只能将日期按照GMT+0时区解析
            Date realDate=formatter.parse(date);
            int[] bookList=venueService.getReservationsBySiteIdAndDate(siteId,realDate);
            for(int i=beginPeriod;i<endPeriod;++i){
                if(bookList[i]!=0){ //时段不可预约
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
                item.setSiteImage(urlPrefix + site.getImage());
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
