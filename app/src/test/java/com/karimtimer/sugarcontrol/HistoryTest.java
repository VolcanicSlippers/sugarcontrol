package com.karimtimer.sugarcontrol;

import com.karimtimer.sugarcontrol.History.HistoryActivity;
import com.karimtimer.sugarcontrol.models.Record;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class HistoryTest {


    private String test1;
    private ArrayList<Double> arrList, arrListZeros, arrayListAllSame, arrBelow, arrAbove, arrIn, arrBelowZero, arrInZero, arrAboveZero;
    private double statsTest1ActualResult1, statsTest1ActualResult2;
    private Record record, recordZero;

    @Before
    public void setup() {
        record = new Record();
        recordZero = new Record();

        record.setTime("11:00");
        record.setDate("23/08/2018");
        record.setSugarLevel("7.6");
        record.setNotes("I'm feeling great.");
        recordZero.setTime("");
        recordZero.setDate("");
        recordZero.setSugarLevel("");
        recordZero.setNotes("");

    }

    //testing setter for Time
    @Test
    public void HistoryTest1() {
        String expected = "11:00";
        String actual = record.getTime();
        assertEquals(expected, actual);
    }
    //testing setter for Date
    @Test
    public void HistoryTest2() {
        String expected = "23/08/2018";
        String actual = record.getDate();
        assertEquals(expected, actual);
    }
    //testing setter for Sugar Levels
    @Test
    public void HistoryTest3() {
        String expected = "7.6";
        String actual = record.getSugarLevel();
        assertEquals(expected, actual);
    }
    //testing setter for Notes
    @Test
    public void HistoryTest4() {
        String expected = "I'm feeling great.";
        String actual = record.getNotes();
        assertEquals(expected, actual);
    }
    //testing setter for Time if null
    @Test
    public void HistoryTest5() {
        String expected = "";
        String actual = recordZero.getTime();
        assertEquals(expected, actual);
    }
    //testing setter for Date if null
    @Test
    public void HistoryTest6() {
        String expected = "";
        String actual = recordZero.getDate();
        assertEquals(expected, actual);
    }
    //testing setter for Sugar Level if null
    @Test
    public void HistoryTest7() {
        String expected = "";
        String actual = recordZero.getSugarLevel();
        assertEquals(expected, actual);
    }
    //testing setter for Notes if null
    @Test
    public void HistoryTest8() {
        String expected = "";
        String actual = recordZero.getNotes();
        assertEquals(expected, actual);
    }
}
