package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.Site;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SiteMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Site record);

    int insertSelective(Site record);

    Site selectByPrimaryKey(Integer id);

    ArrayList<Site> selectByVenueId(Integer venue_id);

    int updateByPrimaryKeySelective(Site record);

    int updateByPrimaryKey(Site record);

    ArrayList<Integer> getVenueSiteIds();

}