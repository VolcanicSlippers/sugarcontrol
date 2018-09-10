package com.karimtimer.sugarcontrol;

import com.karimtimer.sugarcontrol.Bluetooth.Bluetooth;
import com.karimtimer.sugarcontrol.Bluetooth.BluetoothBgl;
import com.karimtimer.sugarcontrol.Record.RecordActivity;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * @author Abdikariim Timer
 *
 * This tests the methods within the recording class named RecordAvticity, which stores the logic in uploading a bgl reading.
 */
public class RecordBGLTest {
    private ArrayList<Double> arrayList, arrayListZero;
    private RecordActivity recordActivity;


    @Before
    public void setup() {
        arrayList = new ArrayList<Double>();
        arrayListZero = new ArrayList<Double>();
        recordActivity = new RecordActivity();

        arrayListZero.add(0.0);
        arrayListZero.add(0.0);
        arrayListZero.add(0.0);
        arrayListZero.add(0.0);

        arrayList.add(1.0);
        arrayList.add(2.0);
        arrayList.add(4.0);
        arrayList.add(7.0);
        arrayList.add(10.0);

    }

    //Testing the Bluetooth setter for the Date
    @Test
    public void bglTest1() {
        double expected = 0.0;
        double actual = recordActivity.overallAvg(arrayListZero);
        assertEquals(expected, actual);
    }
    //Testing the Bluetooth setter for its bgl reading
    @Test
    public void bglTest2() {
        double expected = 4.8;
        double actual = recordActivity.overallAvg(arrayList);
        assertEquals(expected, actual);
    }

}
