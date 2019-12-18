package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LuoChengLing
 */
@Service("ManagerService")
public class ManagerService {
    private ManagerMapper managerDao;

    @Autowired
    public void setManagerDao(ManagerMapper managerDao) {
        this.managerDao = managerDao;
    }

    public String getManagerName(int managerId){
        return managerDao.selectByPrimaryKey(managerId).getName();
    }
}
