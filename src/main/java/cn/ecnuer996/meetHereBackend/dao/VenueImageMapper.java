package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.VenueImage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueImageMapper {

    int insert(VenueImage record);

    int insertSelective(VenueImage record);

    List<String> getVenueImagesByVenueId(Integer venueId);

    String getVenueCoverByVenueId(Integer venueId);

}