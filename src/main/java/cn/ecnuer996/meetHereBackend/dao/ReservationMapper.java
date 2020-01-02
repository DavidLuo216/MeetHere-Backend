package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public interface ReservationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Reservation record);

    int insertSelective(Reservation record);

    Reservation selectByPrimaryKey(Integer id);

    List<Reservation> selectByUserId(Integer userId);

    List<Reservation> selectBySiteIdAndDate(int siteId, Date date);

    Reservation selectLatestReservationByUserId(int userId);

    int updateByPrimaryKeySelective(Reservation record);

    int updateByPrimaryKey(Reservation record);

    ArrayList<Integer> getSiteIdsOfReservations();

    boolean cancelReservation(int hash);

    ArrayList<Reservation> getGoingReservation();

}