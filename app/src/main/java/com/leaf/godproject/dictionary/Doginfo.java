package com.leaf.godproject.dictionary;

import java.io.Serializable;

public class Doginfo implements Serializable {
    private int did;
    //    private int dimage;
    private String dtype;
    private String dtitle;
    private String dcontant;
    private String dlike;


    public Doginfo(int id, String type, String title , String contant, String like) {
        this.did = id;
        this.dtype = type;
        this.dtitle = title;
        this.dcontant = contant;
        this.dlike = like;
    }

    public Doginfo(String type, String title , String contant, String like) {
        this.dtype = type;
        this.dtitle = title;
        this.dcontant = contant;
        this.dlike = like;
    }
    public Doginfo(){}

    public int getId() {
        return did;
    }

    public void setId(int id) {
        this.did = id;
    }

    public String getType() {
        return dtype;
    }

    public void setType(String title) {
        this.dtype = title;
    }

    public String getTitle() {
        return dtitle;
    }

    public void setTitle(String title) {
        this.dtitle = title;
    }

    public String getContent() {
        return dcontant;
    }

    public void setContent(String contant) {
        this.dcontant = contant;
    }

    public String getLike() {
        return dlike;
    }

    public void setLike(String like) { this.dlike = like; }

}