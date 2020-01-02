package cn.ecnuer996.meetHereBackend.transfer;

import java.util.Date;

// 表示显示场馆列表时每个条目的结构---考虑用map来替代？
public class VenueInList{

    public Integer id;
    public String name;
    public String address;
    public Date beginTime;
    public Date endTime;
    public String cover;

    public VenueInList() {

    }

    public VenueInList(Integer id, String name, String address, Date beginTime, Date endTime, String introduction) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.cover = introduction;
    }

}