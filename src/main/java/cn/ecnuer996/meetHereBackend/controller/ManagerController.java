package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.*;
import cn.ecnuer996.meetHereBackend.service.*;
import cn.ecnuer996.meetHereBackend.transfer.ReservationDetail;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
import cn.ecnuer996.meetHereBackend.util.ReservationState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LuoChengLing
 */
@CrossOrigin
@RestController
@Api(tags = "管理员相关接口")
public class ManagerController {

    private UserService userService;

    private SiteService siteService;

    private VenueService venueService;

    private ManagerService managerService;

    private UserAuthService userAuthService;

    private ReservationService reservationService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setUserService(SiteService siteService){
        this.siteService = siteService;
    }

    @Autowired
    public void setVenueService(VenueService venueService){
        this.venueService = venueService;
    }

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Autowired
    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Autowired
    public void setReservationService(ReservationService reservationService){
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

    @ApiOperation("管理员详情")
    @GetMapping("/manager")
    @ApiImplicitParams({@ApiImplicitParam(name = "managerId",value = "管理员ID", required = true)})
    public JsonResult getManagerDetail(@RequestParam int managerId) {
        Manager manager = managerService.getManager(managerId);
        if(manager == null) {
            return new JsonResult(JsonResult.NOT_FOUND, "管理员不存在");
        }
        else {
            Map<String, Object> result=new HashMap<>(1);
            result.put("manager", manager);
            return new JsonResult(result);
        }
    }

    @ApiOperation("管理员登录接口")
    @PostMapping("/manager-sign-in")
    public JsonResult logIn(@RequestBody Manager managerParam){
        String name=managerParam.getName();
        String password=managerParam.getPassword();
        Manager manager=managerService.getManagerByName(name);
        if(manager==null){
            return new JsonResult(JsonResult.NOT_FOUND,"不存在的管理员");
        }
        else if(!manager.getPassword().equals(password)){
            return new JsonResult(JsonResult.FAIL,"密码错误");
        }
        else {
            Map<String,Object> result=new HashMap<>(3);
            result.put("name",manager.getName());
            result.put("avatar", FilePathUtil.URL_MANAGER_AVATAR_PREFIX+manager.getAvatar());
            result.put("id",manager.getId());
            return new JsonResult(result,"登录成功");
        }
    }

    @ApiOperation("获取申请找回密码的用户接口")
    @GetMapping("/get-forget-users")
    public JsonResult getForgetUsers() {
        ArrayList<UserAuth> userAuths = userAuthService.getForgetUserAuths();
        ArrayList<User> users = new ArrayList<>();
        for(UserAuth userAuth : userAuths) {
            users.add(userService.getUserByName(userAuth.getIdentifier()));
        }
        if(users.size() == 0) {
            return new JsonResult(JsonResult.NOT_FOUND,"列表为空");
        }
        else {
            return new JsonResult(users);
        }
    }

    @ApiOperation("同意找回密码接口")
    @GetMapping("/accept-rediscover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JsonResult acceptRediscover(
            @RequestParam("username")String username) {
        userAuthService.acceptRediscover(username);
        return new JsonResult();
    }

    @ApiOperation("拒绝找回密码接口")
    @GetMapping("/refuse-rediscover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true)
    })
    public JsonResult refuseRediscover(
            @RequestParam("username")String username) {
        userAuthService.refuseRediscover(username);
        return new JsonResult();
    }

    @ApiOperation("分页查询进行中订单列表接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询的页号", required = true)})
    @GetMapping(value="/going-orders")
    public JsonResult searchOrders(@RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page) {
        List<Reservation> pre_reservations = reservationService.getGoingReservation();
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

