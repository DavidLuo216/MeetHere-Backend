package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ReservationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @MockBean
    ReservationMapper reservationDao;

    @Test
    void getReservationByUserId() {
        reservationService.getReservationByUserId(1);
        verify(reservationDao,times(1)).selectByUserId(1);
    }

    @Test
    void getSiteIdsOfReservations() {
        reservationService.getSiteIdsOfReservations();
        verify(reservationDao,times(1)).getSiteIdsOfReservations();
    }

    @Test
    void cancelReservation() {
        reservationService.cancelReservation(1,10);
        verify(reservationDao,times(1)).cancelReservation(1+10*100000);
    }

    @Test
    void getGoingReservation() {
        reservationService.getGoingReservation();
        verify(reservationDao.getGoingReservation());
    }
}