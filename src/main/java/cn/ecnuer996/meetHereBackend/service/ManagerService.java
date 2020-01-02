package cn.ecnuer996.meetHereBackend.service;

import cn.ecnuer996.meetHereBackend.dao.ManagerMapper;
import cn.ecnuer996.meetHereBackend.model.Manager;
import cn.ecnuer996.meetHereBackend.util.FilePathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    public Manager getManager(int managerId){
        Manager  manager=managerDao.selectByPrimaryKey(managerId);
        if(manager!=null){
            manager.setAvatar(FilePathUtil.URL_MANAGER_AVATAR_PREFIX+manager.getAvatar());
        }
        return manager;
    }

    public Manager getManagerByName(String name){
        return managerDao.selectByName(name);
    }
}
