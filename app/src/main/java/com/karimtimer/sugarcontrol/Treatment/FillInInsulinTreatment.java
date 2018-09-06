package com.karimtimer.sugarcontrol.Treatment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FillInInsulinTreatment extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private static final String TAG = "check";

    private Button btnDatePickerInsulin, btnTimePickerInsulin, btnSaveInsulinTreatment, btnSave;
    private EditText insulinUnits;
    private Spinner insulinType, insulinBrand;
    private String chosenInsulinType, chosenInsulinBrand;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int emonth, eday, eyear;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, myRef;
    private FirebaseUser mCurrentUser;
    private Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insulin_fill_in_details);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        insulinUnits = (EditText) findViewById(R.id.edit_text_insulin_type);
        insulinType = (Spinner) findViewById(R.id.spinner_insulin_type);
        insulinBrand = (Spinner) findViewById(R.id.spinner_insulin_brand);
        btnSave = findViewById(R.id.btn_save_entry_insulin);

        btnDatePickerInsulin = (Button) findViewById(R.id.btn_date_insulin);
        btnTimePickerInsulin = (Button) findViewById(R.id.btn_time_insulin);

        //btnSaveInsulinTreatment = (Button) findViewById(R.id.btn_save_entry_insulin_treatment);

        btnDatePickerInsulin.setOnClickListener(this);
        btnTimePickerInsulin.setOnClickListener(this);
        insulinType.setOnItemSelectedListener(FillInInsulinTreatment.this);
        insulinBrand.setOnItemSelectedListener(FillInInsulinTreatment.this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User chose the "save" item, take back to home fragment
                Log.d(TAG, "onClick: Attempting to add object to database.");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = mCurrentUser.getUid();
                final String timeInsulinTaken = btnTimePickerInsulin.getText().toString();
                final String dateInsulinTaken = btnDatePickerInsulin.getText().toString();
                final String uploadInsulinType = chosenInsulinType;
                final String uploadInsulinBrand = chosenInsulinBrand;

                if (!timeInsulinTaken.equals("Select Time")) {
                    if (!dateInsulinTaken.equals("Select Date")) {
                        if (!uploadInsulinType.equals("Insulin Type")) {

                            @SuppressWarnings("VisibleForTests") final DatabaseReference newPostInsulin = myRef.push();
                            newPostInsulin.child("time").setValue(timeInsulinTaken);
                            newPostInsulin.child("date").setValue(dateInsulinTaken);
                            newPostInsulin.child("day").setValue(getEday());
                            newPostInsulin.child("month").setValue(getEmonth());
                            newPostInsulin.child("year").setValue(getEyear());
                            newPostInsulin.child("insulin type").setValue(uploadInsulinType);
                            newPostInsulin.child("sugarLevel").setValue(uploadInsulinBrand);
                            Toast.makeText(FillInInsulinTreatment.this, "posted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FillInInsulinTreatment.this, MainActivity.class));

                            finish();
                            Log.i(TAG,"numer 1");


                        }else{
                            //inslulin type is wrong
                            Toast.makeText(FillInInsulinTreatment.this, "Enter an insulin type.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //date taken not right
                        Toast.makeText(FillInInsulinTreatment.this, "Enter a valid date.", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    //time taken wrong
                    Toast.makeText(FillInInsulinTreatment.this, "Enter a valid time", Toast.LENGTH_SHORT).show();

                }


            }
        });

        List<String> categoriesInsulinType = new ArrayList<String>();
        categoriesInsulinType.add("Rapid-acting");
        categoriesInsulinType.add("Short-acting");
        categoriesInsulinType.add("intermediate-acting");
        categoriesInsulinType.add("long-acting");
        categoriesInsulinType.add("mix");

        List<String> categoriesInslinBrands = new ArrayList<String>();
        categoriesInslinBrands.add("Humalog");
        categoriesInslinBrands.add("Humulin");
        categoriesInslinBrands.add("Lantus");
        categoriesInslinBrands.add("Levemir");
        categoriesInslinBrands.add("Novolin");
        categoriesInslinBrands.add("Novolog");

        insulinType.setPrompt("Insulin Type?");
        insulinBrand.setPrompt("Brand name?");

        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesInsulinType);
        final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesInslinBrands);
        Log.d(TAG, "onClick: Attempting to add object to database.");


        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        insulinType.setAdapter(dataAdapter1);
        insulinBrand.setAdapter(dataAdapter2);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("Medication").child(mAuth.getUid());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save_menu, menu);
        return true;    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // User chose the "save" item, take back to home fragment
                Log.d(TAG, "onClick: Attempting to add object to database.");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = mCurrentUser.getUid();
                final String timeInsulinTaken = btnTimePickerInsulin.getText().toString();
                final String dateInsulinTaken = btnDatePickerInsulin.getText().toString();
                final String uploadInsulinType = chosenInsulinType;
                final String uploadInsulinBrand = chosenInsulinBrand;

                if (!timeInsulinTaken.equals("Select Time")) {
                    if (!dateInsulinTaken.equals("Select Date")) {
                        if (!uploadInsulinType.equals("Insulin Type")) {

                            @SuppressWarnings("VisibleForTests") final DatabaseReference newPostInsulin = myRef.push();
                                                        newPostInsulin.child("time").setValue(timeInsulinTaken);
                                                        newPostInsulin.child("date").setValue(dateInsulinTaken);
                                                        newPostInsulin.child("day").setValue(getEday());
                                                        newPostInsulin.child("month").setValue(getEmonth());
                                                        newPostInsulin.child("year").setValue(getEyear());
                                                        newPostInsulin.child("insulin type").setValue(uploadInsulinType);
                                                        newPostInsulin.child("sugarLevel").setValue(uploadInsulinBrand);
                                                        Toast.makeText(FillInInsulinTreatment.this, "posted!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(FillInInsulinTreatment.this, MainActivity.class));

                                                        finish();
                                                        Log.i(TAG,"numer 1");


                        }else{
                            //inslulin type is wrong
                            Toast.makeText(FillInInsulinTreatment.this, "Enter an insulin type.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //date taken not right
                        Toast.makeText(FillInInsulinTreatment.this, "Enter a valid date.", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    //time taken wrong
                    Toast.makeText(FillInInsulinTreatment.this, "Enter a valid time", Toast.LENGTH_SHORT).show();

                }



                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FillInInsulinTreatment.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        // On selecting a spinner item
        int id = view.getId();
        switch (parent.getId()) {
            case R.id.spinner_insulin_type:
                String item1 = parent.getItemAtPosition(position).toString();
                chosenInsulinType = item1;

                Log.i(TAG, "hellooo "+item1);
                break;
            case R.id.spinner_insulin_brand:
                String item2 = parent.getItemAtPosition(position).toString();
                chosenInsulinBrand = item2;

                Log.i(TAG, "hellooo "+item2);
                break;
            default:
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {

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

        if (v == btnDatePickerInsulin) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            btnDatePickerInsulin.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            setEday(dayOfMonth);
                            setEmonth(monthOfYear + 1);
                            setEyear(year);
                        }
                    }, mYear, mMonth, mDay);


            Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
            datePickerDialog.show();
        }
        if (v == btnTimePickerInsulin) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            btnTimePickerInsulin.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
