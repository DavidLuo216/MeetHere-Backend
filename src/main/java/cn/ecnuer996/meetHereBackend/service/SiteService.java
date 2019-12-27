package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.model.Site;
import cn.ecnuer996.meetHereBackend.dao.SiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("SiteService")
public class SiteService {
    @Autowired
    private SiteMapper siteDao;

    public Site getSiteById(int id) {
        return siteDao.selectByPrimaryKey(id);
    }

    public ArrayList<Integer> getVenueSiteIds() {
        return siteDao.getVenueSiteIds();
    }

}
