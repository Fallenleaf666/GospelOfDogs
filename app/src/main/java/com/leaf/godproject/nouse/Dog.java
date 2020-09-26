package com.leaf.godproject.nouse;

public class Dog {
    private String dID;
    private String dNAME;
    private String dBIRTH;
    private String dBLOOD;
    private String dGENDER;
    private String dIMG;
    private String dTYPE;
    private String dMIXTYPE;

    public Dog(String ID, String Name,String Birth,String Blood, String Gender, String Img, String Type,String MixType){
        dID = ID;
        dNAME = Name;
        dGENDER = Gender;
        dTYPE = Type;
        dMIXTYPE=MixType;
        dBIRTH = Birth;
        dBLOOD = Blood;
        dIMG = Img;
    }

    public Dog(String Name,String Birth,String Blood, String Gender, String Img, String Type,String MixType){
        dNAME = Name;
        dGENDER = Gender;
        dTYPE = Type;
        dMIXTYPE=MixType;
        dBIRTH = Birth;
        dBLOOD = Blood;
        dIMG = Img;
    }
    public Dog(){}

    public String getID() {
        return dID;
    }

    public void setID(String ID) {
        this.dID = ID;
    }

    public String getName() {
        return dNAME;
    }

    public void setName(String Name) {
        this.dNAME = Name;
    }

    public String getBirth() { return dBIRTH; }

    public void setBirth(String Birth) { this.dBIRTH = Birth; }

    public String getBlood() { return dBLOOD; }

    public void setBlood(String Blood) { this.dBLOOD = Blood; }


    public String getGender() {
        return dGENDER;
    }

    public void setGender(String Gender) {
        this.dGENDER = Gender;
    }

    public String getImg() {
        return dIMG;
    }

    public void setImg(String Img) {
        this.dIMG = Img;
    }

    public String getType() {
        return dTYPE;
    }

    public void setType(String repeatType) {
        this.dTYPE = repeatType;
    }

    public String getMixType() {
        return dMIXTYPE;
    }

    public void setMixType(String Mixtype) {
        this.dMIXTYPE = Mixtype;
    }
}