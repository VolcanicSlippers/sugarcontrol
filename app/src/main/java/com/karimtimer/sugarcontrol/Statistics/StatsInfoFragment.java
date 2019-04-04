package com.karimtimer.sugarcontrol.Statistics;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Record.RecordEntryForReport;
import com.opencsv.CSVWriter;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Abdikariim Timer
 * This file contains the logic and also the main essence of the statistics tab of Sugar Control.
 */
public class StatsInfoFragment extends Fragment {
    private static final String FILE_NAME = "./report.csv";
    private static final String TAG = "Tab2Fragment";
    private static final String TAGRecordingRow = "Recording Row";
    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final Object[] FILE_HEADER = {"date", "sugarLevel"};
    private double sevenDayAverage;
    private double monthlySugarLevelAverage;

    public double getSevenDayAverage() {
        return sevenDayAverage;
    }

    public void setSevenDayAverage(double sevenDayAverage) {
        this.sevenDayAverage = sevenDayAverage;
    }

    public double getMonthlySugarLevelAverage() {
        return monthlySugarLevelAverage;
    }

    public void setMonthlySugarLevelAverage(double monthlySugarLevelAverage) {
        this.monthlySugarLevelAverage = monthlySugarLevelAverage;
    }

    private double hba1cOverallAvg;
    private double dailyAverage;
    private LineGraphSeries<DataPoint> series1;
    private DatabaseReference mDatabase, mDatabaseHba1c, mDatabaseRange;
    // private static final String TAG = "AddToDatabase";
    private ArrayList<Double> sugarLevelsAL; //todays blood glucose level readings
    private ArrayList<Double> belowRangeEntries;
    private ArrayList<Double> aboveRangeEntries;
    private ArrayList<Double> inRangeEntries;
    private ArrayList<Double> sugarLevelsToday;
    private ArrayList<Double> sugarLevelsMonthly; //a month of blood glucose level readings
    private ArrayList<Double> hba1cOverallAvgAL;
    private ArrayList<Double> sevenDaysOfBgl; //seven days of blood glucose level readings
    private ArrayList<Integer> timeAL;
    private boolean csv_status = false;

    private ArrayList<RecordEntryForReport> recordEntryAL;
    private int individualSugarLevel;
    private Map sugars, sugarsFake;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private TextView tv, txtSevenDayAvg, txtMonthDayAvg, txtHbA1cAvg,
            dataTxtNumOfEntriesToday, dataTxtEntriesInRange, dataTxtEntriesBelowRange,
            dataTxtEntriesAboveRange, txtDailyAvg, dataTxtMaxEntry, dataTxtMinEntry, dataTxtStd;
    private Button btnExportReport;
    private int upperRange;
    private int lowerRange;
    private int numberOfEntriesDaily;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab_2, container, false);

        txtDailyAvg = (TextView) view.findViewById(R.id.data_txt_avg_today_bgl);
        txtSevenDayAvg = (TextView) view.findViewById(R.id.data_weekly_bgl_stat);
        txtMonthDayAvg = (TextView) view.findViewById(R.id.data_monthly_bgl_stat);
        dataTxtNumOfEntriesToday = (TextView) view.findViewById(R.id.data_txt_main_screen_number_of_entries_today);
        dataTxtEntriesInRange = (TextView) view.findViewById(R.id.data_txt_entries_in_range);
        dataTxtEntriesBelowRange = (TextView) view.findViewById(R.id.data_txt_main_screen_entries_below_range);
        dataTxtEntriesAboveRange = (TextView) view.findViewById(R.id.data_txt_main_screen_entries_above_range);
        btnExportReport = (Button) view.findViewById(R.id.btn_export_report);
        dataTxtMaxEntry = view.findViewById(R.id.data_txt_glucose_max);
        dataTxtMinEntry = view.findViewById(R.id.data_txt_glucose_min);
        dataTxtStd = view.findViewById(R.id.data_standard_deviation);

        storagePermitted(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Record").child("SugarLevel").child(mCurrentUser.getUid());
        mDatabaseHba1c = FirebaseDatabase.getInstance().getReference().child("Record").child("HbA1c").child(mCurrentUser.getUid());
        mDatabaseRange = FirebaseDatabase.getInstance().getReference().child("Range").child("SugarLevel").child(mCurrentUser.getUid());
        mDatabase.keepSynced(true);

        setLowerRange(4);
        setUpperRange(10);
        sugarLevelsAL = new ArrayList<Double>();
        sugarLevelsMonthly = new ArrayList<Double>();
        hba1cOverallAvgAL = new ArrayList<Double>();
        belowRangeEntries = new ArrayList<Double>();
        aboveRangeEntries = new ArrayList<Double>();
        inRangeEntries = new ArrayList<Double>();
        sevenDaysOfBgl = new ArrayList<Double>();
        sugarLevelsToday = new ArrayList<Double>();
        timeAL = new ArrayList<Integer>();
        sugarsFake = new HashMap<Integer, Double>();
        sugars = new TreeMap<Double, Double>();
        recordEntryAL = new ArrayList<RecordEntryForReport>();

        mDatabase.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int counter = 0;
                    int counterDaily = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e(TAG, "lmao" + postSnapshot.child("sugarLevel").getValue());

                        String bglRecordingDay = "";
                        int bglRecordingDayNum = 0;
                        if (!postSnapshot.child("day").getValue().toString().isEmpty()) {
                            bglRecordingDay = postSnapshot.child("day").getValue().toString();
                            bglRecordingDayNum = Integer.parseInt(bglRecordingDay);
                        }

                        String dfd = postSnapshot.child("sugarLevel").getValue().toString();

                        String bglRecordingMonth = postSnapshot.child("month").getValue().toString();
                        int bglRecordingMonthNum = Integer.parseInt(bglRecordingMonth);

                        String bglRecordingYear = postSnapshot.child("year").getValue().toString();
                        int bglRecordingYearNum = Integer.parseInt(bglRecordingYear);

                        String dateOfBglReading = postSnapshot.child("date").getValue().toString();

                        //store into arraylist
                        RecordEntryForReport putIn = new RecordEntryForReport(dateOfBglReading, dfd);
                        getRecordEntryAL().add(putIn);


                        //todays day only
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                        Calendar currentCal = Calendar.getInstance();
                        String currentDay = dateFormat.format(currentCal.getTime());
                        int currentDayNum = Integer.parseInt(currentDay);
                        Log.e(TAG, "todays date is:" + currentDay);

                        //the current month
                        DateFormat mnthFormat = new SimpleDateFormat("MM");
                        Date date = new Date();
                        int currentMonthNum = Integer.parseInt(mnthFormat.format(date));
                        Log.e(TAG, "current Month num is: " + currentMonthNum);

                        //date 7 days ago
                        Calendar calSevenDaysAgo = Calendar.getInstance();
                        Log.e(TAG, "gto" + calSevenDaysAgo.toString());
                        calSevenDaysAgo.add(Calendar.DATE, -7);

                        //  String sevenDaysAgo =
                        int sevenDaysAgoNum = calSevenDaysAgo.get(Calendar.DAY_OF_MONTH);
                        int mnthSevenDaysAgo = calSevenDaysAgo.get(Calendar.MONTH) + 1;
                        Log.e(TAG, "day seven days ago:" + sevenDaysAgoNum);

                        Date currentTime = Calendar.getInstance().getTime();
                        String sugarLevelReading = postSnapshot.child("sugarLevel").getValue().toString();
                        Double currentBgl = Double.parseDouble(sugarLevelReading);

                        RecordEntryForReport recordingRow = new RecordEntryForReport(dateOfBglReading, sugarLevelReading);

                        //recordEntryAL.add(recordingRow);
                        Log.e(TAGRecordingRow, recordEntryAL.toString());

                        int iYear = 1999;
                        int iMonth = mnthSevenDaysAgo; // 1 (months begin with 0)
                        int iDay = 1;

                        // Create a calendar object and set year and month
                        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

                        Log.e(TAG, mnthSevenDaysAgo + "  heloooooooooooooo   " + currentMonthNum);
                        if (mnthSevenDaysAgo != currentMonthNum) {
                            Log.e(TAG, "e");
                            int diffInMonths = Math.abs(currentMonthNum - mnthSevenDaysAgo);
                            if (diffInMonths <= 1) {
                                int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // days in the previous month

                                //minus max of days and the current days to work out how far it is from next month
                                int daysLeftInLastMonth = daysInMonth - sevenDaysAgoNum;
                                int correctionToSevenDays = bglRecordingDayNum + daysLeftInLastMonth;
                                Log.e(TAG, "eff " + "days in month: " + daysInMonth + ", sevenDays ago:" + sevenDaysAgoNum + ", currentDayNum: " + currentDayNum);
                                if (correctionToSevenDays <= 7) {
                                    Log.e(TAG, "money");
                                    if (bglRecordingDayNum <= currentDayNum) {
                                        getSevenDaysOfBgl().add(Double.parseDouble(sugarLevelReading));
                                        Log.e(TAG, "idfgdfgdfgd LAST MONTH EDT:" + getSevenDaysOfBgl().toString());

                                        Log.e(TAG, currentDay + ", " + bglRecordingDay);
                                        //range stuff
                                        if (currentDayNum == bglRecordingDayNum) {
                                            Log.e(TAG, "HERE LAST MONTH EDITION");
                                            //setNumberOfEntriesDaily(counterDaily++);

                                            //add entries to the AL for todays values:
                                            sugarLevelsToday.add(currentBgl);
                                            if (currentBgl < getLowerRange()) {
                                                //bgl lower than range. Insert into entries below range.
                                                getBelowRangeEntries().add(currentBgl);
                                            } else if (currentBgl > getUpperRange()) {
                                                //bgl is instead above range.
                                                getAboveRangeEntries().add(currentBgl);
                                            } else {
                                                getInRangeEntries().add(currentBgl);

                                            }
                                        }
                                    }

                                }
                            }
                        } else {
                            if (sevenDaysAgoNum <= bglRecordingDayNum) {
                                if (bglRecordingDayNum <= currentDayNum) {

                                    getSevenDaysOfBgl().add(Double.parseDouble(sugarLevelReading));
                                    Log.e(TAG, "entries for past seven days:" + getSevenDaysOfBgl().toString());

                                    //range stuff
                                    if (currentDayNum == bglRecordingDayNum) {
                                        Log.e(TAG, "todays bgl levels");
                                        sugarLevelsToday.add(currentBgl);
                                        setNumberOfEntriesDaily(counterDaily++);
                                        if (currentBgl < getLowerRange()) {
                                            //bgl lower than range. Insert into entries below range.
                                            getBelowRangeEntries().add(currentBgl);
                                        } else if (currentBgl > getUpperRange()) {
                                            //bgl is instead above range.
                                            getAboveRangeEntries().add(currentBgl);
                                        } else {
                                            getInRangeEntries().add(currentBgl);
                                        }
                                    }
                                }

                            }

                        }
                        if (currentMonthNum == bglRecordingMonthNum) {
                            sugarLevelsMonthly.add(Double.parseDouble(sugarLevelReading));

                            Log.e(TAG, "it worked! todays reading is:" + sugarLevelsMonthly.toString());

                        }

                    }

                    dailyAverage = dailyAvg(getAboveRangeEntries(), getBelowRangeEntries(), getInRangeEntries());
                    setSevenDayAverage(sevenDayavg(getSevenDaysOfBgl()));
                    setMonthlySugarLevelAverage(monthlyAvg(sugarLevelsMonthly));

                    Log.e(TAG, "seven day average" + sevenDayAverage);
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);
                    Double displaySevenAvgNum = getSevenDayAverage();
                    Log.e(TAG, "THIS IS LIFE" + displaySevenAvgNum);
                    displaySevenAvgNum.doubleValue();
                    txtSevenDayAvg.setText(df.format(displaySevenAvgNum));

                    Log.e(TAG, "mothly average" + monthlySugarLevelAverage);
                    DecimalFormat dfMonthly = new DecimalFormat("#.##");
                    dfMonthly.setRoundingMode(RoundingMode.CEILING);
                    Double displayMonthAvgNum = getMonthlySugarLevelAverage();
                    displayMonthAvgNum.doubleValue();
                    txtMonthDayAvg.setText(dfMonthly.format(displayMonthAvgNum));


                    Log.e(TAG, "daily average" + dailyAverage);
                    DecimalFormat dfDaily = new DecimalFormat("#.##");
                    dfDaily.setRoundingMode(RoundingMode.CEILING);
                    Double displayDailyAvgNum = dailyAverage;
                    displayDailyAvgNum.doubleValue();
                    txtDailyAvg.setText(dfDaily.format(displayDailyAvgNum));

    if(sugarLevelsToday.size()!=0) {


        //find the maximum of todays entries
        double maximumBglToday = getMaxValue(sugarLevelsToday);
        DecimalFormat dfMaxToday = new DecimalFormat("#.##");
        dfMaxToday.setRoundingMode(RoundingMode.CEILING);
        Double displayMaxBglToday = maximumBglToday;
        displayMaxBglToday.doubleValue();
        dataTxtMaxEntry.setText(dfMaxToday.format(displayMaxBglToday));


        //find the mim of todays entries
        double minimumBglToday = getMinValue(sugarLevelsToday);
        DecimalFormat dfMinToday = new DecimalFormat("#.##");
        dfMinToday.setRoundingMode(RoundingMode.CEILING);
        Double displayMinBglToday = minimumBglToday;
        displayMinBglToday.doubleValue();
        dataTxtMinEntry.setText(dfMinToday.format(displayMinBglToday));
        //display the standard deviation value for today
        double stdToday = getStd(sugarLevelsToday);
        DecimalFormat dfStdToday = new DecimalFormat("#.##");
        dfStdToday.setRoundingMode(RoundingMode.CEILING);
        Double displayStdToday = stdToday;
        displayStdToday.doubleValue();
        dataTxtStd.setText(dfStdToday.format(displayStdToday));

    }



                } else {
                    mDatabase.removeEventListener(this);
                }
                int numberOfEntriesinRange = getInRangeEntries().size();
                int numberOfEntriesBelowRange = getBelowRangeEntries().size();
                int numberOfEntriesAboveRange = getAboveRangeEntries().size();
                setNumberOfEntriesDaily(numberOfEntriesAboveRange + numberOfEntriesBelowRange + numberOfEntriesinRange);
                Log.i(TAG, numberOfEntriesAboveRange + "," + numberOfEntriesBelowRange + "," + numberOfEntriesinRange);
                dataTxtNumOfEntriesToday.setText("" + getNumberOfEntriesDaily());
                dataTxtEntriesInRange.setText("" + numberOfEntriesinRange);
                dataTxtEntriesBelowRange.setText("" + numberOfEntriesBelowRange);
                dataTxtEntriesAboveRange.setText("" + numberOfEntriesAboveRange);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

//        mDatabaseRange.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            //    Log.e(TAG, "lmao" + dataSnapshot.child("Above Range").getValue());
//                if(dataSnapshot.child("Above Range").exists()) {
//                    String aboveRangeSize = dataSnapshot.child("Above Range").getValue().toString();
//                    int numAboveRangeSize = Integer.parseInt(aboveRangeSize);
//                    String belowRangeSize = dataSnapshot.child("Below Range").getValue().toString();
//                    int numBelowRangeSize = Integer.parseInt(belowRangeSize);
//                    String inRangeSize = dataSnapshot.child("In Range").getValue().toString();
//                    int numInRangesize = Integer.parseInt(inRangeSize);
//
//
//                    int numOfEntries = numAboveRangeSize + numBelowRangeSize + numInRangesize;
//                    Log.e(TAG, "" + numOfEntries);
//                    dataTxtNumOfEntriesToday.setText(getNumberOfEntriesDaily());
//                    dataTxtEntriesInRange.setText(getInRangeEntries().size());
//                    dataTxtEntriesBelowRange.setText(getBelowRangeEntries().size());
//                    dataTxtEntriesAboveRange.setText(getAboveRangeEntries().size());
//                }
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.e(TAG, "Failed to read app title value.", error.toException());
//            }
//        });


//        mDatabaseHba1c.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        Log.e(TAG, "hba" + postSnapshot.child("HbA1cValue").getValue());
//                            if(postSnapshot.child("HbA1cValue").exists()) {
//                                String sugarLevelReading = postSnapshot.child("HbA1cValue").getValue().toString();
//                                hba1cOverallAvgAL.add(Double.parseDouble(sugarLevelReading));
//                            }
//                    }
//
//                    hba1cOverallAvg = overallAvg(hba1cOverallAvgAL);
//
//                    Log.e(TAG, "hba1c verage" + hba1cOverallAvgAL);
//                    DecimalFormat df = new DecimalFormat("#.###");
//                    df.setRoundingMode(RoundingMode.CEILING);
//                    Double displaySevenAvgNum = hba1cOverallAvg;
//                    displaySevenAvgNum.doubleValue();
//                    txtHbA1cAvg.setText(df.format(displaySevenAvgNum));
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.e(TAG, "Failed to read app title value.", error.toException());
//            }
//        });


        final String csvFilePath = "filename";

        btnExportReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Toast.makeText(getActivity(), "shit.", Toast.LENGTH_SHORT).show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                } else {
                    // Permission has already been granted
                    File root = Environment.getExternalStorageDirectory();
                    File myFile = new File(root + "/SugarControl");
                    myFile.mkdir();
                    try {
                        File storingFile = new File(Environment.getExternalStorageDirectory() + "/SugarControl", "report.csv");
                        //We have to create the CSVPrinter class object
                        FileOutputStream fileOutput = new FileOutputStream(storingFile);
                        OutputStreamWriter writer = new OutputStreamWriter(fileOutput);
                        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Date", "Blood glucose level"));
                        for (int j = 0; j < getRecordEntryAL().size(); j++) {
                            String[] recordRow = new String[2];
                            recordRow[0] = getRecordEntryAL().get(j).getDate();
                            recordRow[1] = getRecordEntryAL().get(j).getSugarLevel();
                            csvPrinter.printRecord((Object[]) recordRow);
                        }
                        Toast.makeText(getActivity(), "saved!", Toast.LENGTH_SHORT).show();
                        csvPrinter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
        });


        return view;
    }
    public static double dailyAvg(ArrayList<Double> aboveRangeEntries, ArrayList<Double> belowRangeEntries, ArrayList<Double> inRangeEntries) {
        ArrayList<Double> overallLot = new ArrayList<Double>();

        overallLot.addAll(aboveRangeEntries);
        overallLot.addAll(belowRangeEntries);
        overallLot.addAll(inRangeEntries);
        double sum = 0.0;
        for (int i = 0; i < overallLot.size(); i++) {
            double currentBgll = overallLot.get(i);
            sum += currentBgll;
        }
        return sum / overallLot.size();
    }

    public int getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(int upperRange) {
        this.upperRange = upperRange;
    }

    public int getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(int lowerRange) {
        this.lowerRange = lowerRange;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void writeToCsv(String csvFilePath) {


        try (Writer csv = Files.newBufferedWriter(Paths.get(FILE_NAME));

             Writer csv2 = new OutputStreamWriter(getActivity().openFileOutput("filename.txt", Context.MODE_PRIVATE));

             CSVWriter writer = new CSVWriter(csv2, CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END);) {

            String[] headerRecord = {"Date", "Blood glucose level"};
            writer.writeNext(headerRecord);
            int i = 0;
            //Write a new student object list to the CSV file
            for (RecordEntryForReport record : getRecordEntryAL()) {
                String[] recordRow = new String[1];
                recordRow[0] = getRecordEntryAL().get(i).getDate();
                recordRow[1] = getRecordEntryAL().get(i).getSugarLevel();
                i++;
                writer.writeNext(recordRow);
            }

            //close the writer
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Method used to calculate the overall average.
     *
     * @param hba1cOverallAvgAL
     * @return
     */
    private double overallAvg(ArrayList<Double> hba1cOverallAvgAL) {
        double sum = 0;
        for (int i = 0; i < hba1cOverallAvgAL.size(); i++) {
            double current = hba1cOverallAvgAL.get(i);
            sum += current;
        }
        return sum / hba1cOverallAvgAL.size();
    }

    public static double monthlyAvg(ArrayList<Double> sugarLevelsMonthly) {
        double sum = 0;
        for (int i = 0; i < sugarLevelsMonthly.size(); i++) {
            double current = sugarLevelsMonthly.get(i);
            sum += current;
        }
        return sum / sugarLevelsMonthly.size();

    }

    public static double sevenDayavg(ArrayList<Double> sugarLevelsAL) {
        double sum = 0;
        for (int i = 0; i < sugarLevelsAL.size(); i++) {
            double current = sugarLevelsAL.get(i);
            sum += current;
        }
        return sum / sugarLevelsAL.size();

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

    private int convertStringToInt(String word) {

        return Integer.parseInt(word);
    }

    public ArrayList<Double> getSevenDaysOfBgl() {
        return sevenDaysOfBgl;
    }

    public void setSevenDaysOfBgl(ArrayList<Double> sevenDaysOfBgl) {
        this.sevenDaysOfBgl = sevenDaysOfBgl;
    }

    public int getNumberOfEntriesDaily() {
        return numberOfEntriesDaily;
    }

    public void setNumberOfEntriesDaily(int numberOfEntriesDaily) {
        this.numberOfEntriesDaily = numberOfEntriesDaily;
    }

    public ArrayList<Double> getBelowRangeEntries() {
        return belowRangeEntries;
    }

    public void setBelowRangeEntries(ArrayList<Double> belowRangeEntries) {
        this.belowRangeEntries = belowRangeEntries;
    }

    public ArrayList<Double> getAboveRangeEntries() {
        return aboveRangeEntries;
    }

    public void setAboveRangeEntries(ArrayList<Double> aboveRangeEntries) {
        this.aboveRangeEntries = aboveRangeEntries;
    }

    public ArrayList<Double> getInRangeEntries() {
        return inRangeEntries;
    }

    public void setInRangeEntries(ArrayList<Double> inRangeEntries) {
        this.inRangeEntries = inRangeEntries;
    }

    public ArrayList<RecordEntryForReport> getRecordEntryAL() {
        return recordEntryAL;
    }

    public void setRecordEntryAL(ArrayList<RecordEntryForReport> recordEntryAL) {
        this.recordEntryAL = recordEntryAL;
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Method to check whether external media available and writable.
     */

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        tv.append("\n\nExternal Media: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Toast.makeText(getActivity(), "permission granted", Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), ":(", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private static final int REQUESTCODE_STORAGE_PERMISSION = 5000;

    private static boolean storagePermitted(Activity activity) {

        Boolean readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (readPermission && writePermission) {
            return true;
        }

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_STORAGE_PERMISSION);
        return false;
    }

    public static Double getMaxValue(ArrayList<Double> arrayList) {

        ArrayList<Double> sortArrayList = arrayList;

        Collections.sort(arrayList);

        Collections.reverse(arrayList);

        return arrayList.get(0);
    }

    public static Double getMinValue(ArrayList<Double> arrayList) {

        ArrayList<Double> sortArrayList = arrayList;

        Collections.sort(arrayList);

        return arrayList.get(0);
    }

    public static double getStd(ArrayList<Double> table) {
        // Step 1:
        double mean = monthlyAvg(table);
        double temp = 0;

        for (int i = 0; i < table.size(); i++) {
            double val = table.get(i);

            // Step 2:
            double squrDiffToMean = Math.pow(val - mean, 2);

            // Step 3:
            temp += squrDiffToMean;
        }

        // Step 4:
        double meanOfDiffs = (double) temp / (double) (table.size());

        // Step 5:
        return Math.sqrt(meanOfDiffs);
    }

}