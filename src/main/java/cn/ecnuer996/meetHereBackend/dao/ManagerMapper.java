package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.Manager;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Manager record);

    int insertSelective(Manager record);

    Manager selectByPrimaryKey(Integer id);

    /**
     * 根据管理员名称查询，管理员名唯一
     * @param name
     * @return
     */
    Manager selectByName(String name);

    int updateByPrimaryKeySelective(Manager record);

    int updateByPrimaryKey(Manager record);
}