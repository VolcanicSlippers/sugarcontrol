package com.karimtimer.sugarcontrol.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Record {

    private String date;
    private String time;
    private String sugarLevel;
    private String insulinUnits;
    private String carbs;
    private String notes;

    public Record(String sugarLevel, String notes, String date, String time, String carbs) {

        this.sugarLevel = sugarLevel;
        this.notes = notes;
        this.date = date;
        this.time = time;
        //this.insulinUnits = InsulinUnits;

    }


    public Record() {

    }

//    public String getInsulinUnits() {
//        return insulinUnits;
//    }
//
//    public void setInsulinUnits(String insulinUnits) {
//        this.insulinUnits = insulinUnits;
//    }

    public String getDate() {
        return date.toString();
    }

    public void setDate(String date) {
        this.date = date.toString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(String sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
