package com.leaf.godproject.Model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String petid;
    private long creattime;


    public Post(String postid, String postimage, String description, String publisher, String petid, long creattime) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.petid = petid;
        this.creattime = creattime;
    }

    public Post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getCreattime() { return creattime; }

    public void setCreattime(long creattime) { this.creattime = creattime; }

    public String getPetid() { return petid; }

    public void setPetid(String petid) { this.petid = petid; }
}
