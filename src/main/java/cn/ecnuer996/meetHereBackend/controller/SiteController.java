package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@CrossOrigin
@RestController
@Api(tags = "场地相关接口")
public class SiteController {
    private SiteService siteService;
    private VenueService venueService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    @Autowired
    public void setVenueService(VenueService venueService) {
        this.venueService = venueService;
    }

    @ApiOperation("指定场地信息查询")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "场地id", required = true) })
    @GetMapping(value="/site-detail")
    public JSONObject getVenues(@RequestParam("id") int id) {
        Site site = siteService.getSiteById(id);
        JSONObject response = new JSONObject();
        if(site == null) {
            response.put("code",404);
            response.put("messages","场地不存在");
            response.put("result",null);
        }
        else {
            response.put("code",200);
            response.put("messages","查询成功");
            JSONObject result = new JSONObject();
            result.put("siteName",site.getName());
            result.put("siteIntro",site.getIntruction());
            result.put("siteUrl", FilePathUtil.URL_SITE_IMAGE_PREFIX + site.getImage());
            result.put("sitePrice",site.getPrice());
            response.put("result",result);
        }
        return response;
    }

    @ApiOperation("指定场地可预约时间段查询")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "场地id", required = true),
                         @ApiImplicitParam(name = "date", value = "日期", required = true)})
    @GetMapping(value="/site-time-list")
    public JSONObject getSiteTimeList(@RequestParam("id")Integer siteId,
                                      @RequestParam("date")String date){
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

}
