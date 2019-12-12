package cn.ecnuer996.meetHereBackend.controller;

import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueImageMapper;
import cn.ecnuer996.meetHereBackend.dao.VenueMapper;
import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.model.Venue;
import cn.ecnuer996.meetHereBackend.model.VenueImage;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author LuoChengLing
 */
@RestController
@Api(tags = "场馆相关接口")
public class VenueController {
    private VenueMapper venueDao;
    private VenueImageMapper venueImageDao;
    private SiteMapper siteDao;
    private SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");

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

    @ApiOperation("新增场馆")
    @PostMapping("/add-venue")
    public JSONObject addVenue(@RequestParam String name,
                               @RequestParam String address,
                               @RequestParam String introduction,
                               @RequestParam String phone,
                               @RequestParam String beginTime,
                               @RequestParam String endTime,
                               @RequestParam MultipartFile[] images) throws ParseException {
        JSONObject response=new JSONObject();
        Venue venue=new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setPhone(phone);
        venue.setIntroduction(introduction);
        venue.setBeginTime(timeFormat.parse(beginTime));
        venue.setEndTime(timeFormat.parse(endTime));
        venueDao.insert(venue);
        venue=venueDao.selectByVenueName(name).get(0);
        int venueId=venue.getId();
        for(MultipartFile image:images){
            VenueImage venueImage=new VenueImage();
            venueImage.setVenueId(venueId);
            String imageName=System.currentTimeMillis()+".jpg";
            venueImage.setImage(imageName);
            try{
                image.transferTo(new File(FilePathUtil.LOCAL_VENUE_COVER_PREFIX+imageName));
            } catch (IOException e) {
                e.printStackTrace();
                response.put("message","保存图片失败");
                return response;
            }
            venueImageDao.insert(venueImage);
        }
        response.put("venueId",venueId);
        response.put("message","请求成功");
        return response;
    }

    @ApiOperation("更新场馆的文本信息")
    @PostMapping("/update-venue")
    public JSONObject updateVenue(@RequestParam int id,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String address,
                                  @RequestParam(required = false) String introduction,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime) throws ParseException {
        JSONObject response=new JSONObject();
        Venue venue=new Venue();
        venue.setId(id);
        venue.setName(name);
        venue.setAddress(address);
        venue.setPhone(phone);
        venue.setIntroduction(introduction);
        venue.setBeginTime(timeFormat.parse(beginTime));
        venue.setEndTime(timeFormat.parse(endTime));
        venueDao.updateByPrimaryKeySelective(venue);
        return response;
    }

    @ApiOperation("为某个场馆增加场地")
    @PostMapping("/add-site")
    public JSONObject addSite(@RequestParam int venueId,
                              @RequestParam String name,
                              @RequestParam String introduction,
                              @RequestParam MultipartFile image,
                              @RequestParam float price){
        JSONObject response=new JSONObject();
        String imageName=System.currentTimeMillis()+".jpg";
        try{
            image.transferTo(new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName));
        } catch (IOException e) {
            e.printStackTrace();
            response.put("message","保存图片失败");
            return response;
        }
        Site site=new Site();
        site.setIntruction(introduction);
        site.setName(name);
        site.setVenueId(venueId);
        site.setPrice(price);
        site.setImage(imageName);
        siteDao.insert(site);
        return response;
    }

    @ApiOperation("更新场地信息")
    @PostMapping("/update-site")
    public JSONObject updateSite(@RequestParam int id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String introduction,
                              @RequestParam(required = false) MultipartFile image,
                              @RequestParam(required = false) Float price){
        JSONObject response=new JSONObject();
        String imageName=System.currentTimeMillis()+".jpg";
        Site site=siteDao.selectByPrimaryKey(id);
        if(image!=null){
            try{
                //删除旧图片
                String oldImage=site.getImage();
                File oldImageFile=new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+oldImage);
                if(oldImageFile.exists()){
                    if(!oldImageFile.delete()){
                        response.put("message","删除旧图片失败");
                        return response;
                    }
                }
                image.transferTo(new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName));
                site.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                response.put("message","更新图片失败");
                return response;
            }
        }
        site.setIntruction(introduction);
        site.setName(name);
        site.setPrice(price);
        siteDao.updateByPrimaryKey(site);
        return response;
    }

    @ApiOperation("删除场地")
    @DeleteMapping("/delete-site")
    public JSONObject deleteSite(@RequestParam int id){
        JSONObject response=new JSONObject();
        Site site=siteDao.selectByPrimaryKey(id);
        String imageName=site.getImage();
        File oldImageFile=new File(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX+imageName);
        if(oldImageFile.exists()){
            if(!oldImageFile.delete()){
                response.put("message","删除旧图片失败");
                return response;
            }
        }
        siteDao.deleteByPrimaryKey(id);
        return response;
    }
}