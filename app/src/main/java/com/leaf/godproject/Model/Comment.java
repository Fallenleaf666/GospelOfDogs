package com.leaf.godproject.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String commentid;
    private long creattime;



    public Comment(String comment, String publisher, String commentid, long creattime) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.creattime = creattime;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public long getCreattime() { return creattime; }

    public void setCreattime(long creattime) { this.creattime = creattime; }
}
