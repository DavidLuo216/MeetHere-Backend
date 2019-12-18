package cn.ecnuer996.meetHereBackend.transfer;

/**
 * 新闻列表中单行记录的结构表示类
 * @author LuoChengLing
 */
public class NewsItem {
    private int id;

    private String manager;

    private String time;

    private String title;

    private String cover;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
