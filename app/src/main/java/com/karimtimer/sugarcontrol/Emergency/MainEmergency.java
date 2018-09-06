package com.karimtimer.sugarcontrol.Emergency;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;


/**
 * Class for the Emergency screen.
 *
 * @author Abdikariim Timer
 */
public class MainEmergency extends FragmentActivity{
    private static final String TAG = "Emergency screen";


    private Toolbar toolbar;
    private Button  btnSetContact, btnCallHypo, btnAddEmergencyContact;
    private EditText newEmergencyNumber;
    private String number;
    private SharedPreferences myPrefs;
    private Boolean setClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hypo_main);

        // Set a Toolbar to replace the ActionBar.
//        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setBackgroundColor(Constant.color);

        myPrefs = getSharedPreferences("emergencyPref", Context.MODE_PRIVATE);


        btnAddEmergencyContact = (Button) findViewById(R.id.btn_add_emergency_contact);
        newEmergencyNumber = (EditText) findViewById(R.id.txt_set_contact);
//        btnInfo = (FButton) findViewById(R.id.btn_hypo_advice);
        btnCallHypo = (Button) findViewById(R.id.btn_call_contact);
        btnSetContact = (Button) findViewById(R.id.btn_set_contact);

        newEmergencyNumber.setVisibility(View.INVISIBLE);
        btnAddEmergencyContact.setVisibility(View.INVISIBLE);
        setClicked = false;

        btnCallHypo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Log.e(TAG, "checking if this works");
                String name = myPrefs.getString("contactNum","emergencyNum");
                intent.setData(Uri.parse("tel:"+name));
                startActivity(intent);
            }
        });


        btnSetContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setClicked == false) {
                    newEmergencyNumber.setVisibility(View.VISIBLE);
                    btnAddEmergencyContact.setVisibility(View.VISIBLE);
                    setClicked = true;
                }else{
                    newEmergencyNumber.setVisibility(View.INVISIBLE);
                    btnAddEmergencyContact.setVisibility(View.INVISIBLE);
                    setClicked = false;
                }
            }
        });

        btnAddEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emergencyNum = newEmergencyNumber.getText().toString();

                if (!emergencyNum.isEmpty()) {
                    //string must contain only numbers between 0-9
                    if (emergencyNum.matches("[0-9]+") && emergencyNum.length() > 2) {
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("contactNum", emergencyNum);
                        editor.apply();
                        newEmergencyNumber.setVisibility(View.INVISIBLE);
                        btnAddEmergencyContact.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainEmergency.this, "Emergency contact saved!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainEmergency.this, "supply an actual number!",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainEmergency.this, "Nothing entered!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainEmergency.this, MainActivity.class));

        finish();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
