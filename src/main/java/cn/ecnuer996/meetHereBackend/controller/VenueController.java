package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.model.VenueImage;
import cn.ecnuer996.meetHereBackend.service.VenueService;
import cn.ecnuer996.meetHereBackend.transfer.VenueInList;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import cn.ecnuer996.meetHereBackend.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LuoChengLing
 */
@CrossOrigin
@RestController
@Api(tags = "场馆相关接口")
public class VenueController {
    private VenueMapper venueDao;
    private VenueService venueService;
    private VenueImageMapper venueImageDao;
    private SiteMapper siteDao;
    private SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");

    @Autowired
    public void setVenueService(VenueService venueService) {
        this.venueService = venueService;
    }

    @Autowired
    public void setVenueService(VenueMapper venueDao) {
        this.venueDao = venueDao;
    }

    @Autowired
    public void setVenueImageDao(VenueImageMapper venueImageDao) {
        this.venueImageDao = venueImageDao;
    }

    @Autowired
    public void setSiteDao(SiteMapper siteDao) {
        this.siteDao = siteDao;
    }

    @ApiOperation("场馆一览")
    @ApiImplicitParams({ @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询页号", required = true)})
    @GetMapping(value="/venues")
    public JsonResult getAllVenues(@RequestParam("segment")Integer segment,
                                   @RequestParam("page")Integer page){
        ArrayList<VenueInList> pre_venues = venueService.getAllVenues();
        int num_of_pages = Math.max((int) Math.ceil(pre_venues.size() / (double) segment), 1);
        ArrayList<VenueInList> venues = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_venues.size()); ++i){
            venues.add(pre_venues.get(i));
        }
        Map<String,Object> result=new HashMap<>(segment);
        result.put("num_of_pages", num_of_pages);
        result.put("venues",venues);
        return new JsonResult(result,"查询成功");
    }

    @ApiOperation("场馆关键字搜索")
    @ApiImplicitParams({ @ApiImplicitParam(name = "name", value = "关键词", required = true),
                         @ApiImplicitParam(name = "segment", value = "每页条数", required = true),
                         @ApiImplicitParam(name = "page", value = "待查询页号", required = true)})
    @GetMapping(value="/venue")
    public JsonResult getVenues(@RequestParam("name") String name,
                                @RequestParam("segment")Integer segment,
                                @RequestParam("page")Integer page){
        ArrayList<Venue> pre_venues = venueService.getVenueByName("%" + name + "%");
        int num_of_pages = Math.max((int) Math.ceil(pre_venues.size() / (double) segment), 1);
        ArrayList<VenueInList> venues = new ArrayList<>();
        for(int i = Math.max(page * segment,0); i < Math.min(page * segment + segment, pre_venues.size()); ++i){
            Venue v = pre_venues.get(i);
            venues.add(new VenueInList(v.getId(), v.getName(), v.getAddress(), v.getBeginTime(), v.getEndTime(), v.getIntroduction()));
        }
        Map<String,Object> result=new HashMap<>(segment);
        result.put("num_of_pages", num_of_pages);
        result.put("venues",venues);
        return new JsonResult(result,"查询成功");
    }

    @ApiOperation("查询场馆详情")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "场馆id", required = true) })
    @GetMapping(value="/venue-detail")
    public JsonResult getVenueDetail(@RequestParam("id")Integer venueId){
        return new JsonResult(venueService.getVenueDetail(venueId),"查询成功！");
    }

    @ApiOperation("新增场馆")
    @PostMapping(value = "/add-venue",consumes = "multipart/form-data")
    public JsonResult addVenue(@RequestParam String name,
                               @RequestParam String address,
                               @RequestParam String introduction,
                               @RequestParam String phone,
                               @RequestParam String beginTime,
                               @RequestParam String endTime,
                               @RequestParam MultipartFile[] images){
        Venue venue=new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setPhone(phone);
        venue.setIntroduction(introduction);
        try{
            venue.setBeginTime(timeFormat.parse(beginTime));
            venue.setEndTime(timeFormat.parse(endTime));
        }catch (ParseException p){
            p.printStackTrace();
            return new JsonResult(JsonResult.FAIL,"日期格式错误");
        }
        venueDao.insert(venue);
        venue=venueDao.selectByVenueName(name).get(0);
        int venueId=venue.getId();
        for(MultipartFile image:images){
            VenueImage venueImage=new VenueImage();
            venueImage.setVenueId(venueId);
            String imageName= UUID.randomUUID().toString();
            venueImage.setImage(imageName);
            try{
                image.transferTo(new File(FilePathUtil.LOCAL_VENUE_COVER_PREFIX+imageName));
            } catch (IOException e) {
                e.printStackTrace();
                return new JsonResult(JsonResult.FAIL,"保存图片失败");
            }
            venueImageDao.insert(venueImage);
        }
        Map<String,Object> result=new HashMap<>(1);
        result.put("venueId",venueId);
        return new JsonResult(result,"新增场馆成功！");
    }

    @ApiOperation("更新场馆的文本信息")
    @PostMapping("/update-venue")
    public JsonResult updateVenue(@RequestParam int id,
                                  @RequestParam String name,
                                  @RequestParam String address,
                                  @RequestParam String introduction,
                                  @RequestParam String phone,
                                  @RequestParam String beginTime,
                                  @RequestParam String endTime) throws ParseException {
        Venue venue=new Venue();
        venue.setId(id);
        venue.setName(name);
        venue.setAddress(address);
        venue.setPhone(phone);
        venue.setIntroduction(introduction);
        venue.setBeginTime(timeFormat.parse(beginTime));
        venue.setEndTime(timeFormat.parse(endTime));
        venueDao.updateByPrimaryKeySelective(venue);
        return new JsonResult();
    }

    @ApiOperation("为某个场馆增加场地")
    @PostMapping("/add-site")
    public JsonResult addSite(@RequestParam int venueId,
                              @RequestParam String name,
                              @RequestParam String introduction,
                              @RequestParam MultipartFile image,
                              @RequestParam float price){
        String imageName=UUID.randomUUID().toString();
        if(image!=null){
            try{
                image.transferTo(new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName));
            } catch (IOException e) {
                e.printStackTrace();
                return new JsonResult(JsonResult.FAIL,"保存图片失败");
            }
        }
        Site site=new Site();
        site.setIntruction(introduction);
        site.setName(name);
        site.setVenueId(venueId);
        site.setPrice(price);
        site.setImage(imageName);
        siteDao.insert(site);
        return new JsonResult();
    }

    @ApiOperation("更新场地信息")
    @PostMapping("/update-site")
    public JsonResult updateSite(@RequestParam int id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String introduction,
                              @RequestParam(required = false) MultipartFile image,
                              @RequestParam(required = false) Float price){
        String imageName=UUID.randomUUID().toString();
        Site site=siteDao.selectByPrimaryKey(id);
        if(image!=null){
            try{
                //删除旧图片
                String oldImage=site.getImage();
                File oldImageFile=new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+oldImage);
                if(oldImageFile.exists()){
                    if(!oldImageFile.delete()){
                        return new JsonResult(JsonResult.FAIL,"删除旧图片失败");
                    }
                }
                image.transferTo(new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName));
                site.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                return new JsonResult(JsonResult.FAIL,"更新图片失败");
            }
        }
        site.setIntruction(introduction);
        site.setName(name);
        site.setPrice(price);
        siteDao.updateByPrimaryKeySelective(site);
        return new JsonResult();
    }

    @ApiOperation("删除场地")
    @DeleteMapping("/delete-site")
    public JsonResult deleteSite(@RequestParam int id){
        Site site=siteDao.selectByPrimaryKey(id);
        String imageName=site.getImage();
        File oldImageFile=new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName);
        if(oldImageFile.exists()){
            if(!oldImageFile.delete()){
                return new JsonResult(JsonResult.FAIL,"删除旧图片失败");
            }
        }
        siteDao.deleteByPrimaryKey(id);
        return new JsonResult();
    }
}
