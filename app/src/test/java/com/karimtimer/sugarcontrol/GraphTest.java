package com.karimtimer.sugarcontrol;


import com.karimtimer.sugarcontrol.Statistics.GraphViewFragment;
import com.karimtimer.sugarcontrol.Statistics.StatsInfoFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * @author Abdikariim Timer
 *
 * This test class provides tests for the methods contained in the java class named GraphViewFragment.java
 */
public class GraphTest {

    private String test1;
    private ArrayList<Double> arrList, arrListZeros, arrayListAllSame, arrBelow, arrAbove, arrIn, arrBelowZero, arrInZero, arrAboveZero;
    private double statsTest1ActualResult1, statsTest1ActualResult2;

    @Before
    public void setup() {
        arrList = new ArrayList<Double>();
        arrList.add(1.0);
        arrList.add(2.0);
        arrList.add(3.0);
        arrList.add(4.0);

        arrListZeros = new ArrayList<Double>();
        arrListZeros.add(0.0);
        arrListZeros.add(0.0);
        arrListZeros.add(0.0);

        arrayListAllSame = new ArrayList<Double>();
        arrayListAllSame.add(2.0);
        arrayListAllSame.add(2.0);
        arrayListAllSame.add(2.0);
        arrayListAllSame.add(2.0);
        arrayListAllSame.add(2.0);

        statsTest1ActualResult1 = 2.5;
        statsTest1ActualResult2 = 0.0;

        arrBelow = new ArrayList<Double>();
        arrBelow.add(1.0);
        arrBelow.add(2.0);
        arrBelow.add(3.0);
        arrBelow.add(3.9);

        arrIn = new ArrayList<Double>();
        arrIn.add(4.5);
        arrIn.add(5.0);
        arrIn.add(5.5);
        arrIn.add(6.0);
        arrIn.add(6.5);

        arrAbove =  new ArrayList<Double>();
        arrAbove.add(8.0);
        arrAbove.add(8.5);
        arrAbove.add(9.5);

        arrBelowZero = new ArrayList<Double>();
        arrBelowZero.add(0.0);
        arrBelowZero.add(0.0);
        arrBelowZero.add(0.0);

        arrInZero = new ArrayList<Double>();
        arrInZero.add(0.0);

        arrAboveZero = new ArrayList<Double>();
        arrAboveZero.add(0.0);
        arrAboveZero.add(0.0);


    }

    /**
     * What will I need to test from GraphViewFragment?
     * -dayAvg
     */
    //Testing dayAvg
    @Test
    public void statsTest1() {
        double actual = GraphViewFragment.dayavg(arrList);
        assertEquals(statsTest1ActualResult1, actual);
    }

    //Testing dayAvg, by checking if all zeros is fine.
    @Test
    public void statsTest2() {
        double actual = GraphViewFragment.dayavg(arrListZeros);
        assertEquals(statsTest1ActualResult2, actual);
    }

}
