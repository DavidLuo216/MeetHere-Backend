package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.Venue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface VenueMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Venue record);

    int insertSelective(Venue record);

    Venue selectByPrimaryKey(Integer id);

    ArrayList<Venue> selectByVenueName(String name);

    List<Venue> selectAllVenues();

    int updateByPrimaryKeySelective(Venue record);

    int updateByPrimaryKey(Venue record);

}