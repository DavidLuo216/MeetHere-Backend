package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.service.SiteService;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
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
import java.util.HashMap;
import java.util.Map;

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
    public JsonResult getVenues(@RequestParam("id") int id) {
        Site site = siteService.getSiteById(id);
        if(site == null) {
            return new JsonResult(JsonResult.NOT_FOUND,"场地不存在");
        }
        else {
            Map<String,Object> result=new HashMap<>(4);
            result.put("siteName",site.getName());
            result.put("siteIntro",site.getIntruction());
            result.put("siteUrl", FilePathUtil.URL_SITE_IMAGE_PREFIX + site.getImage());
            result.put("sitePrice",site.getPrice());
            return new JsonResult(result,"查询成功");
        }
    }

    @ApiOperation("指定场地可预约时间段查询")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "场地id", required = true),
                         @ApiImplicitParam(name = "date", value = "日期", required = true)})
    @GetMapping(value="/site-time-list")
    public JsonResult getSiteTimeList(@RequestParam("id")Integer siteId,
                                      @RequestParam("date")String date){
        try{
            Map<String,Object> result=new HashMap<>(1);
            result.put("times",venueService.getSiteTimes(siteId,date));
            return new JsonResult(result,"查询成功");
        }catch(ParseException parseException){
            return new JsonResult(JsonResult.FAIL,"日期参数格式不正确");
        }
    }

}
