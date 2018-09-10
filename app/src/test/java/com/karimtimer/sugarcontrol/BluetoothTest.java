package com.karimtimer.sugarcontrol;

import com.karimtimer.sugarcontrol.Bluetooth.Bluetooth;
import com.karimtimer.sugarcontrol.Bluetooth.BluetoothBgl;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

public class BluetoothTest {
    private Bluetooth bluetooth;
    private BluetoothBgl bluetoothBgl, bluetoothBglIncorrectValue;


    @Before
    public void setup() {
        bluetooth = new Bluetooth();
        bluetoothBgl = new BluetoothBgl();
        bluetoothBglIncorrectValue = new BluetoothBgl();

        bluetooth.setDate("12/08/2018");
        bluetooth.setSrgLvl("5.0");
        bluetoothBgl.setSgrLvl("5.0");
        bluetoothBglIncorrectValue.setSgrLvl("hello");
    }

    //Testing the Bluetooth setter for the Date
    @Test
    public void bluetoothTest1() {
        String expected = "12/08/2018";
        String actual = bluetooth.getDate();
        assertEquals(expected, actual);
    }
    //Testing the Bluetooth setter for its bgl reading
    @Test
    public void bluetoothTest2() {
        String expected = "5.0";
        String actual = bluetooth.getSrgLvl();
        assertEquals(expected, actual);
    }
    //Testing the Bluetooth setter for its bgl reading
    @Test
    public void bluetoothTest3() {
        String expected = "5.0";
        String actual = bluetoothBgl.getSgrLvl();
        assertEquals(expected, actual);
    }
    //Testing the Bluetooth to see if a non numeric value will be valid or not. Test should not work.
    @Test
    public void bluetoothTest4() {
        String expected = "5.0";
        String actual = bluetoothBglIncorrectValue.getSgrLvl();
        assertNotSame(expected,actual);
    }

}
