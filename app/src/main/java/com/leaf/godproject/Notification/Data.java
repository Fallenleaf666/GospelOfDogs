package com.leaf.godproject.Notification;

public class Data {
    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String imgurl;
    private String postid;
    private int ispost;
//    public Data(String user, int icon, String body, String title, String sented) {
//        this.user = user;
//        this.icon = icon;
//        this.body = body;
//        this.title = title;
//        this.sented = sented;
//    }
//追加ISPOST跟POSTID
//    public Data(String user, int icon, String body, String title, String sented, String imgurl) {
//        this.user = user;
//        this.icon = icon;
//        this.body = body;
//        this.title = title;
//        this.sented = sented;
//        this.imgurl = imgurl;
//    }

    public Data(String user, int icon, String body, String title, String sented, String imgurl, String postid, int ispost) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.imgurl = imgurl;
        this.postid = postid;
        this.ispost = ispost;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public int getIspost() {
        return ispost;
    }

    public void setIspost(int ispost) {
        this.ispost = ispost;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}