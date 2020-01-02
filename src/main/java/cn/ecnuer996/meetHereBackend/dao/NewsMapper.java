package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.News;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface NewsMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(News record);

    int insertSelective(News record);

    News selectByPrimaryKey(Integer id);

    /**
     * 数据库设置了新闻标题唯一，可通过标题查询
     * 用于新增新闻后获取其id以便保存新闻图片
     * @param title
     * @return
     */
    News selectByTitle(String title);

    int updateByPrimaryKeySelective(News record);

    int updateByPrimaryKey(News record);
	
	ArrayList<News> selectAllNews();

}