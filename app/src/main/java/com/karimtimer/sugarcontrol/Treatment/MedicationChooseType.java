package com.karimtimer.sugarcontrol.Treatment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.util.ArrayList;
import java.util.List;

public class MedicationChooseType extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "check";

    private Spinner spinnerMedicationChosenType;
    private String  chosenMedicationType;
    private int pos;
    private Button advanceFromMedicationType;
    private Toolbar toolbar;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, myRef;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        String medTypeCheckStr = myRef.child("medType").toString();

        if(medTypeCheckStr.equals("Insulin")){
            Log.i(TAG, "hello" + medTypeCheckStr);
            startActivity(new Intent(MedicationChooseType.this, FillInInsulinTreatment.class));
            finish();
        }
        if(medTypeCheckStr.equals("medication")){
            startActivity(new Intent(MedicationChooseType.this, FillMedicationTreatment.class));
            finish();
        }
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medication_choose_type_fragment);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        spinnerMedicationChosenType = findViewById(R.id.medication_type_spinner);
        advanceFromMedicationType = (Button) findViewById(R.id.advance_from_medication_type);



        spinnerMedicationChosenType.setOnItemSelectedListener(MedicationChooseType.this);


        List<String> categoriesMedicationType = new ArrayList<String>();
        categoriesMedicationType.add("Insulin");
        categoriesMedicationType.add("Medication");

        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesMedicationType);

        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerMedicationChosenType.setAdapter(dataAdapter1);







        advanceFromMedicationType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenMedicationType.equals("Insulin")){
                   // final String medType = "Insulin";
                    myRef.child("medType").setValue("Insulin");
                    startActivity(new Intent(MedicationChooseType.this, FillInInsulinTreatment.class));
                    finish();
                }
                if(chosenMedicationType.equals("Medication")){
                  //  final String medType = "medication";
                    myRef.child("medType").setValue("medication");
                    startActivity(new Intent(MedicationChooseType.this, FillMedicationTreatment.class));
                    finish();
                }

            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        // On selecting a spinner item
                String item1 = parent.getItemAtPosition(position).toString();
                pos = position;
                chosenMedicationType = item1;
                Log.i(TAG, "hellooo "+item1);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MedicationChooseType.this, MainActivity.class));

        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void onNothingSelected(AdapterView<?> arg0) {

    }
    }
