package com.karimtimer.sugarcontrol;


import com.karimtimer.sugarcontrol.Record.RecordActivity;
import com.karimtimer.sugarcontrol.Record.RecordHba1c;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * @author Abdikariim Timer
 * This Java class provides the testsfor the class used for recording HbA1c results.
 */
public class RecordHbA1cTest {
    private ArrayList<Double> arrayList, arrayListZero;
    private RecordHba1c recordHba1c;


    @Before
    public void setup() {
        arrayList = new ArrayList<Double>();
        arrayListZero = new ArrayList<Double>();
        recordHba1c = new RecordHba1c();

        arrayListZero.add(0.0);
        arrayListZero.add(0.0);
        arrayListZero.add(0.0);
        arrayListZero.add(0.0);

        arrayList.add(10.0);
        arrayList.add(8.0);
        arrayList.add(5.0);
        arrayList.add(7.0);
        arrayList.add(14.0);
    }

    //Testing the Bluetooth setter for the Date
    @Test
    public void hba1cTest1() {
        double expected = 0.0;
        double actual = recordHba1c.overallAvg(arrayListZero);
        assertEquals(expected, actual);
    }
    //Testing the Bluetooth setter for its bgl reading
    @Test
    public void hba1cTest2() {
        double expected = 8.8;
        double actual = recordHba1c.overallAvg(arrayList);
        assertEquals(expected, actual);
    }

}
