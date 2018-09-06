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
 * This test class will test the classes within the package Statistics. This includes the classes:
 * - tab1,
 * - tab2.
 *
 * The following class contains 20 distinct test cases, and passes all of them.
 */
public class StatisticTest {

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

    /**
     * What will I need to test from StatsInfoFragment?
     * -getMax
     * -getMin
     * -getStd
     * -monthlyAvg
     * -sevenDayAvg
     * -dailyAvg
     * -
     */
    /////Testing getMax/////////


    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest3() {
        double actual = StatsInfoFragment.getMaxValue(arrList);
        assertEquals(4.0, actual);
    }
    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest4() {
        double actual = StatsInfoFragment.getMaxValue(arrListZeros);
        assertEquals(0.0, actual);
    }
    //Testing getMax, checking if the maximal value is given if all the values are the same.
    @Test
    public void statsTest5() {
        double actual = StatsInfoFragment.getMaxValue(arrayListAllSame);
        assertEquals(2.0, actual);
    }


    /////Testing getMin/////////



    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest6() {
        double actual = StatsInfoFragment.getMinValue(arrList);
        assertEquals(1.0, actual);
    }
    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest7() {
        double actual = StatsInfoFragment.getMinValue(arrListZeros);
        assertEquals(0.0, actual);
    }
    //Testing getMax, checking if the maximal value is given if all the values are the same.
    @Test
    public void statsTest8() {
        double actual = StatsInfoFragment.getMinValue(arrayListAllSame);
        assertEquals(2.0, actual);
    }

    /////Testing getstd/////////



    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest9() {
        double actual = StatsInfoFragment.getStd(arrList);
        double expected = 1.118033988749895;
        assertEquals(expected, actual);
    }
    //Testing getMax, checking if the maximal value is given.
    @Test
    public void statsTest10() {
        double actual = StatsInfoFragment.getStd(arrListZeros);
        assertEquals(0.0, actual);
    }
    //Testing getMax, checking if the maximal value is given if all the values are the same.
    @Test
    public void statsTest11() {
        double actual = StatsInfoFragment.getStd(arrayListAllSame);
        assertEquals(0.0, actual);
    }


    ////// Testing  monthlyAvg //////////////


    @Test
    public void statsTest12() {
        double actual = StatsInfoFragment.monthlyAvg(arrList);
        double expected = 2.5;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest13() {
        double actual = StatsInfoFragment.monthlyAvg(arrListZeros);
        double expected = 0.0;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest14() {
        double actual = StatsInfoFragment.monthlyAvg(arrayListAllSame);
        double expected = 2.0;
        assertEquals(expected, actual);
    }

    ////// Testing  sevenDayAvg //////////////

    @Test
    public void statsTest15() {
        double actual = StatsInfoFragment.sevenDayavg(arrList);
        double expected = 2.5;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest16() {
        double actual = StatsInfoFragment.sevenDayavg(arrListZeros);
        double expected = 0.0;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest17() {
        double actual = StatsInfoFragment.sevenDayavg(arrayListAllSame);
        double expected = 2.0;
        assertEquals(expected, actual);
    }

    ////// Testing  dailyAvg //////////////

    @Test
    public void statsTest18() {
        double actual = StatsInfoFragment.dailyAvg(arrBelow, arrIn, arrAbove);
        double expected = 5.283333333333333;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest19() {
        double actual = StatsInfoFragment.dailyAvg(arrBelowZero,arrInZero,arrAboveZero);
        double expected = 0.0;
        assertEquals(expected, actual);
    }
    @Test
    public void statsTest20() {
        double actual = StatsInfoFragment.dailyAvg(arrBelowZero, arrIn,arrAboveZero);
        double expected = 2.75;
        assertEquals(expected, actual);
    }
}
