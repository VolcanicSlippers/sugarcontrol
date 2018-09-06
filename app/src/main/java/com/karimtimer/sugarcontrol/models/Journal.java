package com.karimtimer.sugarcontrol.models;

public class Journal {
    private String title, desc, uid;
    public Journal(String title, String desc, String uid) {
        this.title = title;
        this.desc = desc;
        this.uid = uid;
    }
    public Journal() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}