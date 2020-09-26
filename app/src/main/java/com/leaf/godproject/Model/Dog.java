package com.leaf.godproject.Model;

public class Dog {
    private String master;
    private String id;
    private String name;
    private String birth;
    private String blood;
    private String gender;
    private String imgurl;
    private String type;
    private String mixtype;

//    改long
    private long creattime;

    public Dog(String master, String id, String name, String birth, String blood, String gender, String imgurl, String type, String mixtype, long creattime) {
        this.master = master;
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.blood = blood;
        this.gender = gender;
        this.imgurl = imgurl;
        this.type = type;
        this.mixtype = mixtype;
        this.creattime = creattime;
    }

    public Dog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMixtype() {
        return mixtype;
    }

    public void setMixtype(String mixtype) {
        this.mixtype = mixtype;
    }

    public long getCreattime() {
        return creattime;
    }

    public void setCreattime(long creattime) {
        this.creattime = creattime;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
