package com.leaf.godproject.Model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private boolean ispost;
    private long creattime;

    public Notification(String userid, String text, String postid, boolean ispost, long creattime) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
        this.creattime = creattime;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    public long getCreattime() { return creattime; }

    public void setCreattime(long creattime) { this.creattime = creattime; }

}
