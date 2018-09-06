package com.karimtimer.sugarcontrol.Record;

public class RecordEntryForReport {


    private String date;
    private String sugarLevel;
    private String note;

    public RecordEntryForReport(String date, String sugarLevel){
        this.date = date;
        this.sugarLevel = sugarLevel;
//        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(String sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

//    public String getNote() {
//        return note;
//    }
//
//    public void setNote(String note) {
//        this.note = note;
//    }

    @Override
    public String toString() {
        StringBuffer retStrBuf = new StringBuffer();
        retStrBuf.append("id = " + this.date);
        retStrBuf.append(" , userName = " + this.sugarLevel);
        return retStrBuf.toString();
    }

}
