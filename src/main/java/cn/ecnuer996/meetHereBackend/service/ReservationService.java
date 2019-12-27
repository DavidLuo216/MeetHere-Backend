package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.model.Reservation;
import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
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

}
