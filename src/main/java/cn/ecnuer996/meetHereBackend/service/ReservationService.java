package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import cn.ecnuer996.meetHereBackend.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ReservationService")
public class ReservationService {

    @Autowired
    private ReservationMapper reservationDao;

    public List<Reservation> getReservationByUserId(int id) {
        return reservationDao.selectByUserId(id);
    }

    public ArrayList<Integer> getSiteIdsOfReservations() {
        return reservationDao.getSiteIdsOfReservations();
    }

    public boolean cancelReservation(Integer userId, Integer reservationId) {
        reservationDao.cancelReservation(userId + reservationId * 100000);
        return true;
    }

    public ArrayList<Reservation> getGoingReservation() {
        return reservationDao.getGoingReservation();
    }

}
