package com.karimtimer.sugarcontrol.models;

public class RecordCheck {

    /**
     * the time will be in the right format if it meets the following criteria:
     * 4 integer digits
     * length is strictly 5 (including the colon)
     * @param time
     * @return
     */
    public static boolean checkTimeFormat(String time){
        if(time.length() != 5){
            return false;
        }
        return true;
    }

    public boolean checkBloodGlucoseReading(){
        return true;
    }

}
