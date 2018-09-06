package com.karimtimer.sugarcontrol.Statistics;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Abdikariim Timer
 * Class for the graphical visualisation of user data.
 */
public class GraphViewFragment extends Fragment {
    private static final String TAG = "Tab1Fragment";


    private BarGraphSeries<DataPoint> series, series1, seriesPieChart;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
    private DatabaseReference mDatabase;
    // private static final String TAG = "AddToDatabase";
    private ArrayList<Double> sugarLevelsAL;
    private boolean dailyClicked, rangeClicked;
    private GraphView graphDaily;
    private PieChart pieRange, mChart;
    private ArrayList<Date> dateAL;
    private ArrayList<Double> sugarLevelDailyAL, bgl1DayAgo, bgl2DaysAgo, bgl3DaysAgo, bgl4DaysAgo, bgl5DaysAgo, bgl6DaysAgo, belowRange, aboveRange, inRange, entries;
    private ArrayList<Double> timeDailyAL;
    private ArrayList<Integer> timeAL;
    private int individualSugarLevel;
    private Map sugars, sugarsFake;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private Spinner spinnerDay, spinnerGraphType;
    private boolean globalCheckEventListener;
    private Boolean dayOrMonthBool;
    private Button daily, btnRange;
    private String[] xValues = {"Data 1", "Data 2", "Data 3"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab_1, container, false);
         mChart = (PieChart) view.findViewById(R.id.pie_chart);

        // spinnerDay = (Spinner) view.findViewById(R.id.spinner_number_of_days);
        //spinnerGraphType = (Spinner) view.findViewById(R.id.spinner_type_of_graph);
        series1 = new BarGraphSeries<DataPoint>();
//        btnRange = view.findViewById(R.id.btn_range);
        daily = (Button) view.findViewById(R.id.btn_graph_daily);
        // weekly = (Button) view.findViewById(R.id.btn_graph_weekly);
        graphDaily = (GraphView) view.findViewById(R.id.graph);
//        pieRange = (PieChart) view.findViewById(R.id.pie_chart);

        series = new BarGraphSeries<>();
        graphDaily.setVisibility(View.INVISIBLE);
//        pieRange.setVisibility(View.INVISIBLE);


        entries = new ArrayList<Double>();
        belowRange = new ArrayList<Double>();
        inRange = new ArrayList<Double>();
        aboveRange = new ArrayList<Double>();
        sugarLevelDailyAL = new ArrayList<Double>();
        bgl1DayAgo = new ArrayList<Double>();
        bgl2DaysAgo = new ArrayList<Double>();
        bgl3DaysAgo = new ArrayList<Double>();
        bgl4DaysAgo = new ArrayList<Double>();
        bgl5DaysAgo = new ArrayList<Double>();
        bgl6DaysAgo = new ArrayList<Double>();
        timeDailyAL = new ArrayList<Double>();

        List<String> spinnerDayCategories = new ArrayList<String>();
        spinnerDayCategories.add("daily");
        spinnerDayCategories.add("weekly");
        spinnerDayCategories.add("yearly");
        dayOrMonthBool = true;
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Record").child("SugarLevel").child(mCurrentUser.getUid());
        mDatabase.keepSynced(true);

        dateAL = new ArrayList<Date>();
        sugarLevelsAL = new ArrayList<Double>();
        timeAL = new ArrayList<Integer>();
        sugarsFake = new HashMap<Integer, Double>();
        sugars = new TreeMap<Double, Double>();
        mDatabase.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;
                int counter = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    String daySugarLevelReading = postSnapshot.child("day").getValue().toString();
                    int daySugarLevelReadingNum = Integer.parseInt(daySugarLevelReading);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                    Calendar currentCal = Calendar.getInstance();
                    String currentdate = dateFormat.format(currentCal.getTime());
                    Log.e(TAG, "=======" + currentdate);

                    int currendateNum = Integer.parseInt(currentdate);
                    currentCal.add(Calendar.DATE, -1);
                    String sevenDaysAgo = dateFormat.format(currentCal.getTime());
                    int sevenDaysAgoNum = Integer.parseInt(sevenDaysAgo);
                    Date currentTime = Calendar.getInstance().getTime();

                    if (daySugarLevelReadingNum == currendateNum) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        //  double duration = ((60 * hours + minutes) * 60)/3600; //add up our values
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
//                        double hhmmtimeDaily = Double.parseDouble(formatt.format(d1));
                        getSugarLevelDailyAL().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());

                    }

                    int yesterdayDay = currendateNum - 1;
                    Log.e(TAG, "yesterday date:" + yesterdayDay);
                    //yesterday
                    if (daySugarLevelReadingNum == yesterdayDay) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        //  double duration = ((60 * hours + minutes) * 60)/3600; //add up our values
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
                        getBgl1DayAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }
                    int twoDaysAgo = currendateNum - 2;
                    Log.e(TAG, "yesterday date vr2:" + twoDaysAgo);

                    //two days ago
                    if (daySugarLevelReadingNum == twoDaysAgo) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
                        getBgl2DaysAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }
                    int threeDaysAgo = currendateNum - 3;
                    //three days ago
                    if (threeDaysAgo == daySugarLevelReadingNum) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
                        getBgl3DaysAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }

                    int fourDaysAgo = currendateNum - 4;
                    //four days ago
                    if (fourDaysAgo == daySugarLevelReadingNum) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        //  double duration = ((60 * hours + minutes) * 60)/3600; //add up our values
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
//                        double hhmmtimeDaily = Double.parseDouble(formatt.format(d1));
                        getBgl4DaysAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }

                    int fiveDaysAgo = currendateNum - 5;
                    //four days ago
                    if (fiveDaysAgo == daySugarLevelReadingNum) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        //  double duration = ((60 * hours + minutes) * 60)/3600; //add up our values
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
                        getBgl5DaysAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }
                    int sixdaysAgo = currendateNum - 6;
                    //four days ago
                    if (sixdaysAgo == daySugarLevelReadingNum) {
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);

                        String timeString = postSnapshot.child("time").getValue().toString();
                        String yearStr = postSnapshot.child("year").getValue().toString();
                        String monthStr = postSnapshot.child("month").getValue().toString();
                        String dayStr = postSnapshot.child("day").getValue().toString();
                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hourOfDay = Integer.parseInt(units[0]); //first element
                        int minute = Integer.parseInt(units[1]); //second element
                        //  double duration = ((60 * hours + minutes) * 60)/3600; //add up our values
                        int year = Integer.parseInt(yearStr);
                        int month = Integer.parseInt(monthStr);
                        int day = Integer.parseInt(dayStr);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, day, hourOfDay, minute);
                        Date d1 = cal.getTime();
                        DateFormat formatt = new SimpleDateFormat("HH:MM");
                        getBgl6DaysAgo().add(sugarLevel);
                        getDateAL().add(d1);
                        Log.e(TAG, "d1" + d1.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });


        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                if (dailyClicked == false) {
                    graphDaily.setVisibility(View.VISIBLE);
                    // styling series
                    Collections.sort(dateAL);
                    Log.e(TAG, "time" + dateAL);
                    Log.e(TAG, "bgl" + getSugarLevelDailyAL().toString());
                    Log.e(TAG, "jake u wot" + getSugarLevelDailyAL().size());
                    series = new BarGraphSeries<>(getDataPoint());
                    graphDaily.addSeries(series);
                    GridLabelRenderer gridLabel = graphDaily.getGridLabelRenderer();
                    gridLabel.setHorizontalAxisTitle("Days Ago");

                    series.setSpacing(25);

                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(getResources().getColor(R.color.colorPrimaryDark));


                    series.setTitle("Daily");
                    series.setColor(getResources().getColor(R.color.colorPrimaryDark));


                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(1999, 0, 0, 0, 0);
                    Date d3 = cal2.getTime();

                    graphDaily.getViewport().setYAxisBoundsManual(true);
                    graphDaily.getViewport().setMinY(0);
                    graphDaily.getViewport().setMaxY(15);
                    graphDaily.getGridLabelRenderer().setNumHorizontalLabels(7);
                    graphDaily.getGridLabelRenderer().setNumVerticalLabels(14);
                    series.setSpacing(0);
                    // legend
                    series.setTitle("per Day");
                    graphDaily.getLegendRenderer().setBackgroundColor(getResources().getColor(R.color.white));
                    graphDaily.getLegendRenderer().setVisible(true);
                    graphDaily.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    dailyClicked = true;
                } else {
                    graphDaily.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "daily graph removed" + dailyClicked);
                    graphDaily.removeAllSeries();
                    dailyClicked = false;
                }


            }
        });

//
//        btnRange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//
//                if (rangeClicked == false) {
//                    mChart.setVisibility(View.VISIBLE);
//
//
//
//
//
//                    List<PieEntry> pieChartEntries = new ArrayList<>();
//
//                    pieChartEntries.add(new PieEntry(18.5f, "Green"));
//                    pieChartEntries.add(new PieEntry(26.7f, "Yellow"));
//                    pieChartEntries.add(new PieEntry(24.0f, "Red"));
//                    pieChartEntries.add(new PieEntry(30.8f, "Blue"));
//
//                    PieDataSet set = new PieDataSet(pieChartEntries, "Emotion Results");
//                    PieData data = new PieData(set);
//                    mChart.setData(data);
//                    mChart.invalidate();
//
//
//                    rangeClicked = true;
//                } else {
//                    pieRange.setVisibility(View.INVISIBLE);
//                    Log.e(TAG, "daily graph removed" + rangeClicked);
//                    rangeClicked = false;
//                }
//
//
//            }
//        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }


    private void fillGraph() {

    }

    public ArrayList<Double> getSugarLevelDailyAL() {
        return sugarLevelDailyAL;
    }

    public void setSugarLevelDailyAL(ArrayList<Double> sugarLevelDailyAL) {
        this.sugarLevelDailyAL = sugarLevelDailyAL;
    }

    public ArrayList<Double> getTimeDailyAL() {
        return timeDailyAL;
    }

    public void setTimeDailyAL(ArrayList<Double> timeDailyAL) {
        this.timeDailyAL = timeDailyAL;
    }

    private int convertStringToInt(String word) {

        return Integer.parseInt(word);
    }


    public ArrayList<Date> getDateAL() {
        return dateAL;
    }

    public void setDateAL(ArrayList<Date> dateAL) {
        this.dateAL = dateAL;
    }


    public static double dayavg(ArrayList<Double> al) {
        double sum = 0;
        for (int i = 0; i < al.size(); i++) {
            double current = al.get(i);
            sum += current;
        }
        if(sum==0){
            return 0;
        }else {
            return sum / al.size();
        }
    }


    public DataPoint[] getDataPoint() {
        int n = getSugarLevelDailyAL().size();
        DataPoint[] dp = new DataPoint[7];

        dp[0] = new DataPoint(0, dayavg(getSugarLevelDailyAL()));
        dp[1] = new DataPoint(1, dayavg(getBgl1DayAgo()));
        dp[2] = new DataPoint(2, dayavg(getBgl2DaysAgo()));
        dp[3] = new DataPoint(3, dayavg(getBgl3DaysAgo()));
        dp[4] = new DataPoint(4, dayavg(getBgl4DaysAgo()));
        dp[5] = new DataPoint(5, dayavg(getBgl5DaysAgo()));
        dp[6] = new DataPoint(6, dayavg(getBgl6DaysAgo()));

        Log.e(TAG, "size: " + n + ", Jake " + Arrays.toString(dp));
        return dp;
    }

    public int compare(Date date1, Date date2) {
        return date1.compareTo(date2) * -1;
    }


    public DataPoint[] getMontthlyDataPoint() {
        int n = 7;
        DataPoint[] dp = new DataPoint[n];
        for (int i = 0; i < n; i++) {

            /**
             * How to:
             * Here, for the daily option, I will offer a a data point array of size n=24. I will then check if an entry is suitable for an hoour, and place it in their. If it is not, then it should be blank.
             */
            getDateAL().get(i);

            Calendar jonVal = Calendar.getInstance();
            jonVal.setTime(getDateAL().get(i));
            int dayee = jonVal.get(Calendar.DAY_OF_MONTH);
            Log.e(TAG, "jean " + dayee);

            long secs = (getDateAL().get(i).getTime());
            //if the time is between 00:00 - 01:00
            Log.e(TAG, "JOHN BOY" + dayee);
            dp[i] = new DataPoint(getDateAL().get(i), getSugarLevelDailyAL().get(i));
        }

        Log.e(TAG, "size: " + n + ", Jake " + Arrays.toString(dp));
        return dp;
    }

    public ArrayList<Double> getBgl6DaysAgo() {
        return bgl6DaysAgo;
    }

    public ArrayList<Double> getBgl5DaysAgo() {
        return bgl5DaysAgo;
    }

    public ArrayList<Double> getBgl4DaysAgo() {
        return bgl4DaysAgo;
    }

    public ArrayList<Double> getBgl3DaysAgo() {
        return bgl3DaysAgo;
    }

    public ArrayList<Double> getBgl2DaysAgo() {
        return bgl2DaysAgo;
    }

    public ArrayList<Double> getBgl1DayAgo() {
        return bgl1DayAgo;
    }
}
