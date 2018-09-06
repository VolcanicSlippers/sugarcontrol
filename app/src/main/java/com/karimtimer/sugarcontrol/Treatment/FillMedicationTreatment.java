package com.karimtimer.sugarcontrol.Treatment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillMedicationTreatment extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "check";

    private Toolbar toolbar;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, myRef;
    private FirebaseUser mCurrentUser;
    private Button btnTimeMedicationTaken, btnDateMedicationTaken, btnSave;
    private Map<String, List<String>> medicationList;
    private List<String> totalPills;
    private int emonth, eday, eyear;
    private int mYear, mMonth, mDay, mHour, mMinute;


    public List<String> getTotalPills() {
        return totalPills;
    }

    public void setTotalPills(List<String> totalPills) {
        this.totalPills = totalPills;
    }

    public Map<String, List<String>> getMedicationList() {
        return medicationList;
    }

    public void setMedicationListe(Map<String, List<String>> medicationList) {
        this.medicationList = medicationList;
    }


    public class Item {
        boolean checked;
        //Drawable ItemDrawable;
        String ItemString;
        Item(String t, boolean b){
           // ItemDrawable = drawable;
            ItemString = t;
            checked = b;
        }

        public boolean isChecked(){
            return checked;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        //ImageView icon;
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.medicine_row, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
               // viewHolder.icon = (ImageView) rowView.findViewById(R.id.rowImageView);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

           // viewHolder.icon.setImageDrawable(list.get(position).ItemDrawable);
            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);

            /*
            viewHolder.checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(position).checked = b;

                    Toast.makeText(getApplicationContext(),
                            itemStr + "onCheckedChanged\nchecked: " + b,
                            Toast.LENGTH_LONG).show();
                }
            });
            */

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;
                    String addItemStr = itemStr;
                    if(newState ==true) {
                        getTotalPills().add(addItemStr);
                        getMedicationList().put("pills", totalPills);
                        Log.i(TAG, getMedicationList().toString());
                        Toast.makeText(getApplicationContext(),
                                itemStr + "setOnClickListener\nchecked: " + newState,
                                Toast.LENGTH_LONG).show();
                    }else{
                        for(int i=0; i<getTotalPills().size();i++){
                            String currentPill = getTotalPills().get(i);
                            if(itemStr.equals(currentPill)){
                                getTotalPills().remove(i);
                            }
                        }
                    }
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

    //Button btnLookup;
    List<Item> items;
    ListView listView;
    ItemsListAdapter myItemsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.medication_fill_in_details);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView)findViewById(R.id.medicine_list_view);
        //btnLookup = (Button)findViewById(R.id.lookup);

        btnTimeMedicationTaken = (Button) findViewById(R.id.btn_time_medication);
        btnDateMedicationTaken = (Button) findViewById(R.id.btn_date_medication);
        btnSave = findViewById(R.id.btn_medicine_save);

        btnDateMedicationTaken.setOnClickListener(this);
        btnTimeMedicationTaken.setOnClickListener(this);


         totalPills = new ArrayList<String>();
        medicationList = new HashMap<String, List<String>>();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("Medication").child(mAuth.getUid());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User chose the "save" item, take back to home fragment
                Log.d(TAG, "onClick: Attempting to add object to database.");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = mCurrentUser.getUid();
                final String timeMedicationTaken = btnTimeMedicationTaken.getText().toString();
                final String dateMedicationTaken = btnDateMedicationTaken.getText().toString();


                @SuppressWarnings("VisibleForTests") final DatabaseReference newPostMedicalPills = myRef.push();

                newPostMedicalPills.setValue(getMedicationList());
                newPostMedicalPills.child("pills").child("time").setValue(timeMedicationTaken);
                newPostMedicalPills.child("pills").child("date").setValue(dateMedicationTaken);
                Toast.makeText(FillMedicationTreatment.this, "saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FillMedicationTreatment.this, MainActivity.class));

                finish();
            }
        });

        initItems();
        myItemsListAdapter = new ItemsListAdapter(this, items);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(FillMedicationTreatment.this,
                        ((Item)(parent.getItemAtPosition(position))).ItemString,
                        Toast.LENGTH_LONG).show();
            }});

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
                final String timeMedicationTaken = btnTimeMedicationTaken.getText().toString();
                final String dateMedicationTaken = btnDateMedicationTaken.getText().toString();


                @SuppressWarnings("VisibleForTests") final DatabaseReference newPostMedicalPills = myRef.push();

                newPostMedicalPills.setValue(getMedicationList());
                newPostMedicalPills.child("pills").child("time").setValue(timeMedicationTaken);
                newPostMedicalPills.child("pills").child("date").setValue(dateMedicationTaken);
                Toast.makeText(FillMedicationTreatment.this, "saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FillMedicationTreatment.this, MainActivity.class));

                finish();

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void initItems(){
        items = new ArrayList<Item>();

       // TypedArray arrayDrawable = getResources().obtainTypedArray(R.array.resicon);
        TypedArray arrayText = getResources().obtainTypedArray(R.array.restext);

        for(int i=0; i<arrayText.length(); i++){
            //Drawable d = arrayDrawable.getDrawable(i);
            String s = arrayText.getString(i);
            boolean b = false;
            Item item = new Item( s, b);
            items.add(item);
        }

       // arrayDrawable.recycle();
        arrayText.recycle();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(FillMedicationTreatment.this, MainActivity.class));

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

        if (v == btnDateMedicationTaken) {

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

                            btnDateMedicationTaken.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            setEday(dayOfMonth);
                            setEmonth(monthOfYear + 1);
                            setEyear(year);
                        }
                    }, mYear, mMonth, mDay);


            Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
            datePickerDialog.show();
        }
        if (v == btnTimeMedicationTaken) {

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

                            btnTimeMedicationTaken.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


}
