package com.leaf.godproject.alarm;

// Reminder class
public class Reminder {
    private int mID;
    private String mTitle;
    private String mAlarmtype;
    private String mAlarmstat;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;


    public Reminder(int ID, String Title,String Alarmtype,String Alarmstat, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active){
        mID = ID;
        mTitle = Title;
        mAlarmtype = Alarmtype;
        mAlarmstat = Alarmstat;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
    }

    public Reminder(String Title,String Alarmtype,String Alarmstat, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active){
        mTitle = Title;
        mAlarmtype = Alarmtype;
        mAlarmstat = Alarmstat;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
    }

    public Reminder(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


//    暫時
    public String getAlarmtype() { return mAlarmtype; }

    public void setAlarmtype(String alarmtype) { mAlarmtype = alarmtype; }

    public String getAlarmstat() { return mAlarmstat; }

    public void setAlarmstat(String alarmstat) { mAlarmstat = alarmstat; }


    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String repeatNo) {
        mRepeatNo = repeatNo;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String repeat) {
        mRepeat = repeat;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }
}
