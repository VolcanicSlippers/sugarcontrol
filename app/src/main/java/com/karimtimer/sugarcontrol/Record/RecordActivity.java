package com.karimtimer.sugarcontrol.Record;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RecordActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "Record";

    private Button btnDatePicker, btnTimePicker, btnSaveEntry;
    private EditText txtDate, txtTime, txtSugarLevel, txtCarbs, txtNotes;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private StorageReference storage;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef, myRef2, myRef3;
    private FirebaseUser mCurrentUser;
    private Toolbar toolbar;
    private ArrayList<Double> inRangeAL;
    private ArrayList<Double> belowRangeAL;
    private ArrayList<Double> aboveRangeAL;
    private ArrayList<Double> avgToday;
    private int lowerRange, upperRange;
    private int emonth, eday, eyear;
    private ImageView exclamationMark;
    private TextView bglRemark, whatTimeTxt, notesTitle;
    private long mLastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        //Toolbar for saving items
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setBackgroundColor(Constant.color);


        setLowerRange(4);
        setUpperRange(10);

        //initialising objects
        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        // txtDate = (EditText) findViewById(R.id.in_date);
        //  txtTime = (EditText) findViewById(R.id.in_time);

        txtSugarLevel = (EditText) findViewById(R.id.edit_sugar_level);
        txtSugarLevel.addTextChangedListener(filterTextWatcher);

        bglRemark = (TextView) findViewById(R.id.remark_on_blood_glucose_level);
        whatTimeTxt = (TextView) findViewById(R.id.what_time_title);
        notesTitle = (TextView) findViewById(R.id.text_notes);
        //txtCarbs = (EditText) findViewById(R.id.edit_carbs);
        btnSaveEntry = (Button) findViewById(R.id.btn_save_entry);
        txtNotes = (EditText) findViewById(R.id.edit_notes);
        //exclamationMark = (ImageView) findViewById(R.id.exclamation_mark_sug_level);


        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnSaveEntry.setOnClickListener(this);

        storage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        myRef2 = mFirebaseDatabase.getReference().child("Current Record").child("SugarLevel").child(mAuth.getUid());
        myRef3 =  mFirebaseDatabase.getReference().child("Range").child("SugarLevel").child(mAuth.getUid());


       String sugarLevelText = txtSugarLevel.getText().toString();
//       if(!sugarLevelText.equals("")) {
//           double sugLevel = Double.parseDouble(sugarLevelText);
//           if (sugLevel > 10) {
//               Log.e(TAG, "WE'RE INNNN");
//               exclamationMark.setVisibility(View.VISIBLE);
//               exclamationMark.setImageResource(R.drawable.ic_priority_high_black_24dp);
//               exclamationMark.setColorFilter(Color.RED);
//
//           }
//       }



        inRangeAL = new ArrayList<Double>();
        aboveRangeAL = new ArrayList<Double>();
        belowRangeAL = new ArrayList<Double>();
        avgToday = new ArrayList<Double>();

        myRef.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int counter = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String daySugarLevelReading = postSnapshot.child("day").getValue().toString();
                    int daySugarLevelReadingNum = Integer.parseInt(daySugarLevelReading);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                    Calendar currentCal = Calendar.getInstance();
                    String currentdate = dateFormat.format(currentCal.getTime());


                    if(daySugarLevelReading.equals(currentdate)){
                        String sugarLevelString = postSnapshot.child("sugarLevel").getValue().toString();
                        double sugarLevel = Double.parseDouble(sugarLevelString);
                        avgToday.add(sugarLevel);
                        Log.i(TAG, "sugar levels: " + avgToday.toString());

                        if(sugarLevel < getLowerRange()){
                            belowRangeAL.add(sugarLevel);
                            Log.i(TAG, "below range AL" + belowRangeAL.toString());
                        }if(sugarLevel > getUpperRange()){
                            aboveRangeAL.add(sugarLevel);
                            Log.i(TAG, "above range AL" + aboveRangeAL.toString());
                        }if(sugarLevel >= getLowerRange() && sugarLevel <= getUpperRange()){
                            inRangeAL.add(sugarLevel);
                            Log.i(TAG, "overall in range AL" + inRangeAL.toString());
                        }
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });


        btnSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    //    String recordNumber = counter;
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = mCurrentUser.getUid();
                    final String time = btnTimePicker.getText().toString();
                    final String date = btnDatePicker.getText().toString();
                    //  final String carbs = txtCarbs.getText().toString();
                    final String sugarLevel = txtSugarLevel.getText().toString();
                    final String newNote = txtNotes.getText().toString();
                    int inRangeSize = getInRangeAL().size();
                    int belowRangeSize = getBelowRangeAL().size();
                    int aboveRangeSize = getAboveRangeAL().size();
                    double todaysAvg = overallAvg(avgToday);

                    //if(checkTimeFormat(date) == true) {
                    if (!time.equals("Select Time")) {
                        if (!date.equals("Select Date")) {
                            if (!sugarLevel.equals("")) {

                                @SuppressWarnings("VisibleForTests") final DatabaseReference newPost = myRef.push();
                                @SuppressWarnings("VisibleForTests") final DatabaseReference currentRecordPost = myRef2;
                                @SuppressWarnings("VisibleForTests") final DatabaseReference rangePost = myRef3;

                                newPost.child("time").setValue(time);
                                newPost.child("date").setValue(date);
                                newPost.child("day").setValue(getEday());
                                newPost.child("month").setValue(getEmonth());
                                newPost.child("year").setValue(getEyear());
                                // newPost.child("carbs").setValue(carbs);
                                newPost.child("sugarLevel").setValue(sugarLevel);
                                newPost.child("notes").setValue(newNote);

                                currentRecordPost.child("time").setValue(time);
                                currentRecordPost.child("date").setValue(date);
                                currentRecordPost.child("day").setValue(getEday());
                                currentRecordPost.child("month").setValue(getEmonth());
                                currentRecordPost.child("year").setValue(getEyear());
                                currentRecordPost.child("Recording").setValue(sugarLevel);

                                rangePost.child("In Range").setValue(inRangeSize);
                                rangePost.child("Below Range").setValue(belowRangeSize);
                                rangePost.child("Above Range").setValue(aboveRangeSize);
                                rangePost.child("Todays avg").setValue(todaysAvg);


                                //post the stuff
                                Log.e(TAG, "numer 1");
                                startActivity(new Intent(RecordActivity.this, MainActivity.class));

                                finish();


                            } else {
                                Toast.makeText(RecordActivity.this, "Enter a Blood Glucose reading!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(RecordActivity.this, "Enter a valid date.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(RecordActivity.this, "Enter a valid time.", Toast.LENGTH_SHORT).show();

                    }

                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }

            //   }
        });

    }

    public ArrayList<Double> getInRangeAL() {
        return inRangeAL;
    }

    public void setInRangeAL(ArrayList<Double> inRangeAL) {
        this.inRangeAL = inRangeAL;
    }

    public ArrayList<Double> getBelowRangeAL() {
        return belowRangeAL;
    }

    public void setBelowRangeAL(ArrayList<Double> belowRangeAL) {
        this.belowRangeAL = belowRangeAL;
    }

    public ArrayList<Double> getAboveRangeAL() {
        return aboveRangeAL;
    }

    public void setAboveRangeAL(ArrayList<Double> aboveRangeAL) {
        this.aboveRangeAL = aboveRangeAL;
    }
    public int getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(int lowerRange) {
        this.lowerRange = lowerRange;
    }

    public int getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(int upperRange) {
        this.upperRange = upperRange;
    }
    private int getEmonth() {
        return emonth;
    }

    private void setEmonth(int emonth) {
        this.emonth = emonth;
    }

    private int getEday() {
        return eday;
    }

    public void setEday(int eday) {
        this.eday = eday;
    }

    private int getEyear() {
        return eyear;
    }

    private void setEyear(int eyear) {
        this.eyear = eyear;
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RecordActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            btnDatePicker.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear+1), year));

                            setEday(dayOfMonth);
                            setEmonth(monthOfYear + 1);
                            setEyear(year);
                        }
                    }, mYear, mMonth, mDay);


            Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(RecordActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            btnTimePicker.setText(String.format("%02d:%02d", hourOfDay, minute));                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public static String count(String number) {
        String myString = number;
        int foo = Integer.parseInt(myString);
        foo++;
        String returnNum = Integer.toString(foo);
        return returnNum;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RecordActivity.this, MainActivity.class));

        finish();
    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Setting my title bar
//        (RecordActivity.this)
//                .setActionBarTitle("Record an Entry");
//
//
//        if (getView() == null) {
//            return;
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                    getActivity().finish();
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void toastMessage(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            if(!charSequence.toString().isEmpty()) {
//                double sugarLevelBeingChecked = Double.parseDouble(charSequence.toString());
//                whatTimeTxt.setVisibility(View.VISIBLE);
//                //txtDate.setVisibility(View.VISIBLE);
//                btnTimePicker.setVisibility(View.VISIBLE);
//                btnDatePicker.setVisibility(View.VISIBLE);
//                notesTitle.setVisibility(View.VISIBLE);
//                txtNotes.setVisibility(View.VISIBLE);
//                btnSaveEntry.setVisibility(View.VISIBLE);
//                if (sugarLevelBeingChecked < 4) {
//                    // exclamationMark.setVisibility(View.VISIBLE);
//                    bglRemark.setText("level is below normal range");
//                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));
//                }
//                if (sugarLevelBeingChecked > 10) {
//                    //  exclamationMark.setVisibility(View.VISIBLE);
//                    bglRemark.setText("level is above normal range");
//                    bglRemark.setTextColor(getResources().getColor(R.color.danger));
//                }if(sugarLevelBeingChecked >4 && sugarLevelBeingChecked <10){
//                    //  exclamationMark.setVisibility(View.INVISIBLE);
//                    bglRemark.setVisibility(View.INVISIBLE);
//                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));
//
//                }if(charSequence.toString().equals("")){
//                    whatTimeTxt.setVisibility(View.INVISIBLE);
//                    //txtDate.setVisibility(View.VISIBLE);
//                    btnTimePicker.setVisibility(View.INVISIBLE);
//                    btnDatePicker.setVisibility(View.INVISIBLE);
//                    notesTitle.setVisibility(View.INVISIBLE);
//                    txtNotes.setVisibility(View.INVISIBLE);
//                    btnSaveEntry.setVisibility(View.INVISIBLE);
//                }
//            }

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            if(!charSequence.toString().isEmpty()) {
//                double sugarLevelBeingChecked = Double.parseDouble(charSequence.toString());
//                whatTimeTxt.setVisibility(View.VISIBLE);
//                //txtDate.setVisibility(View.VISIBLE);
//                btnTimePicker.setVisibility(View.VISIBLE);
//                btnDatePicker.setVisibility(View.VISIBLE);
//                notesTitle.setVisibility(View.VISIBLE);
//                txtNotes.setVisibility(View.VISIBLE);
//                btnSaveEntry.setVisibility(View.VISIBLE);
//                if (sugarLevelBeingChecked < 4) {
//                   // exclamationMark.setVisibility(View.VISIBLE);
//                    bglRemark.setText("level is below normal range");
//                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));
//                }
//                if (sugarLevelBeingChecked > 10) {
//                  //  exclamationMark.setVisibility(View.VISIBLE);
//                    bglRemark.setText("level is above normal range");
//                    bglRemark.setTextColor(getResources().getColor(R.color.danger));
//                }if(sugarLevelBeingChecked >4 && sugarLevelBeingChecked <10){
//                  //  exclamationMark.setVisibility(View.INVISIBLE);
//                    bglRemark.setVisibility(View.INVISIBLE);
//                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));
//
//                }if(charSequence.toString().equals("")){
//                    whatTimeTxt.setVisibility(View.INVISIBLE);
//                    //txtDate.setVisibility(View.VISIBLE);
//                    btnTimePicker.setVisibility(View.INVISIBLE);
//                    btnDatePicker.setVisibility(View.INVISIBLE);
//                    notesTitle.setVisibility(View.INVISIBLE);
//                    txtNotes.setVisibility(View.INVISIBLE);
//                    btnSaveEntry.setVisibility(View.INVISIBLE);
//                }
//            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!editable.toString().isEmpty()) {
                double sugarLevelBeingChecked = Double.parseDouble(editable.toString());
                whatTimeTxt.setVisibility(View.VISIBLE);
                //txtDate.setVisibility(View.VISIBLE);
                btnTimePicker.setVisibility(View.VISIBLE);
                btnDatePicker.setVisibility(View.VISIBLE);
                notesTitle.setVisibility(View.VISIBLE);
                txtNotes.setVisibility(View.VISIBLE);
                btnSaveEntry.setVisibility(View.VISIBLE);
                if (sugarLevelBeingChecked < 4) {
                    // exclamationMark.setVisibility(View.VISIBLE);
                    bglRemark.setText("level is below normal range");
                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));
                }
                if (sugarLevelBeingChecked > 10 && sugarLevelBeingChecked < Integer.MAX_VALUE) {
                    //  exclamationMark.setVisibility(View.VISIBLE);
                    bglRemark.setText("level is above normal range");
                    bglRemark.setTextColor(getResources().getColor(R.color.danger));
                }if(sugarLevelBeingChecked >4 && sugarLevelBeingChecked <10){
                    //  exclamationMark.setVisibility(View.INVISIBLE);
                    bglRemark.setVisibility(View.INVISIBLE);
                    bglRemark.setTextColor(getResources().getColor(R.color.light_blue));

                }if(editable.toString().equals("")){
                    whatTimeTxt.setVisibility(View.INVISIBLE);
                    //txtDate.setVisibility(View.VISIBLE);
                    btnTimePicker.setVisibility(View.INVISIBLE);
                    btnDatePicker.setVisibility(View.INVISIBLE);
                    notesTitle.setVisibility(View.INVISIBLE);
                    txtNotes.setVisibility(View.INVISIBLE);
                    btnSaveEntry.setVisibility(View.INVISIBLE);
                }
            }
        }
    };


    /**
     * Method used to calculate the overall average.
     * @param al
     * @return
     */
    private double overallAvg(ArrayList<Double> al) {
        if (al.size() == 0) {
                return 0;
        } else {
            double sum = 0;
            for (int i = 0; i < al.size(); i++) {
                double current = al.get(i);
                sum += current;
            }
            return sum / al.size();
        }
    }
}