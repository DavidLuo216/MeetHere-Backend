package cn.ecnuer996.meetHereBackend.dao;

import cn.ecnuer996.meetHereBackend.model.NewsImageKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsImageMapper {
    int deleteByPrimaryKey(NewsImageKey key);

    int insert(NewsImageKey record);

    int insertSelective(NewsImageKey record);

    /**
     * 查询某个新闻的所有图片
     * @param newsId
     * @return
     */
    List<String> selectNewsImagesByNewsId(Integer newsId);

    /**
     * 查找特定id新闻的第一张图片作为封面
     * @param newsId
     * @return
     */
    String selectNewsCoverByNewsId(Integer newsId);
}