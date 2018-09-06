package com.karimtimer.sugarcontrol.Record;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.util.Calendar;

public class RecordHba1c extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "check";

    private Toolbar toolbar;
    private EditText txtHbA1c, txtNotesHbA1c;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, myRef;
    private FirebaseUser mCurrentUser;
    private Button btnTimeHbA1c, btnDateHbA1c, btnSave;
    private int emonth, eday, eyear;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_hba1c);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtHbA1c = (EditText) findViewById(R.id.edit_hba1c_reading);
        txtNotesHbA1c = (EditText) findViewById(R.id.edit_text_note_hba1c);
        btnTimeHbA1c = (Button) findViewById(R.id.btn_time_hba1c);
        btnDateHbA1c = (Button) findViewById(R.id.btn_date_hba1c);
        btnSave = findViewById(R.id.btnSaveEntry_hba1c);


        btnTimeHbA1c.setOnClickListener(this);
        btnDateHbA1c.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("HbA1c").child(mAuth.getUid());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User chose the "save" item, take back to home fragment
                Log.d(TAG, "onClick: Attempting to add object to database.");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = mCurrentUser.getUid();
                final String hbA1cValue = txtHbA1c.getText().toString();
                final String hbA1cNotes = txtNotesHbA1c.getText().toString();
                final String dateHbA1c = btnDateHbA1c.getText().toString();
                final String timeHbA1c = btnTimeHbA1c.getText().toString();

                @SuppressWarnings("VisibleForTests") final DatabaseReference newPostHbA1c = myRef.push();

                newPostHbA1c.child("HbA1cValue").setValue(hbA1cValue);
                newPostHbA1c.child("HbA1cNotes").setValue(hbA1cNotes);
                newPostHbA1c.child("date").setValue(dateHbA1c);
                newPostHbA1c.child("time").setValue(timeHbA1c);
                Toast.makeText(RecordHba1c.this, "saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RecordHba1c.this, MainActivity.class));

                finish();
            }
        });



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
                final String hbA1cValue = txtHbA1c.getText().toString();
                final String hbA1cNotes = txtNotesHbA1c.getText().toString();
                final String dateHbA1c = btnDateHbA1c.getText().toString();
                final String timeHbA1c = btnTimeHbA1c.getText().toString();

                @SuppressWarnings("VisibleForTests") final DatabaseReference newPostHbA1c = myRef.push();

                newPostHbA1c.child("HbA1cValue").setValue(hbA1cValue);
                newPostHbA1c.child("HbA1cNotes").setValue(hbA1cNotes);
                newPostHbA1c.child("date").setValue(dateHbA1c);
                newPostHbA1c.child("time").setValue(timeHbA1c);
                Toast.makeText(RecordHba1c.this, "saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RecordHba1c.this, MainActivity.class));

                finish();

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RecordHba1c.this, MainActivity.class));

        finish();
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

        if (v == btnDateHbA1c) {

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

                            btnDateHbA1c.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear+1), year));

                            setEday(dayOfMonth);
                            setEmonth(monthOfYear + 1);
                            setEyear(year);
                        }
                    }, mYear, mMonth, mDay);


            Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
            datePickerDialog.show();
        }
        if (v == btnTimeHbA1c) {

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

                            btnTimeHbA1c.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


}
