package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.service.SiteService;
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

@CrossOrigin
@RestController
@Api(tags = "场地相关接口")
public class SiteController {
    private SiteService siteService;

    String urlPrefix="https://ecnuer996.cn/images";

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @ApiOperation("根据场地id查询场地接口")
    @ApiImplicitParams({ @ApiImplicitParam(name = "site_id", value = "场地id", required = true) })
    @GetMapping(value="/site-detail")
    public JSONObject getVenues(@RequestParam("site_id") int id) {
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
            result.put("siteUrl",urlPrefix + site.getImage());
            result.put("sitePrice",site.getPrice());
            response.put("result",result);
        }
        return response;
    }
}
