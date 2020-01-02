package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ManagerMapper;
import cn.ecnuer996.meetHereBackend.model.Manager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
class ManagerServiceTest {

    @Autowired
    ManagerService managerService;

    @MockBean
    ManagerMapper managerDao;

    @Test
    void getManager() {
        Manager manager=new Manager();
        manager.setAvatar("avatar.jpg");
        when(managerDao.selectByPrimaryKey(1)).thenReturn(manager);
        managerService.getManager(1);
        verify(managerDao,times(1)).selectByPrimaryKey(1);
    }

    @Test
    void getManagerByName() {
        managerService.getManagerByName("管理员名");
        verify(managerDao,times(1)).selectByName("管理员名");
    }

}