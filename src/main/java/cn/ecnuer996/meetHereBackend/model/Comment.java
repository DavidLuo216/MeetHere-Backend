package cn.ecnuer996.meetHereBackend.model;

public class Comment {
    private Integer id;

    private String type;

    private Integer userId;

    private String content;

    private Integer link;

    public Comment() {

    }

    public Comment(Integer id, String type, Integer userId, String content, Integer link) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.content = content;
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getLink() {
        return link;
    }

    public void setLink(Integer link) {
        this.link = link;
    }
}