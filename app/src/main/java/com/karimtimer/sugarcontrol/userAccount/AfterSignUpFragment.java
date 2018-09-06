package com.karimtimer.sugarcontrol.userAccount;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.karimtimer.sugarcontrol.R;

public class AfterSignUpFragment extends AppCompatActivity {

    private Button btnToHomeActivity, btnToTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_activity);
//        btnToHomeActivity = (Button) findViewById(R.id.btn_to_home);
//        btnToTour = findViewById(R.id.btn_tour);
//       // btnToTips = (Button) findViewById(R.id.btn_from_initial_boot_to_initial_tips);
//
//        btnToHomeActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(AfterSignUpFragment.this, MainActivity.class));
//                finish();
//
//            }
//        });
//
//        btnToTour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(AfterSignUpFragment.this, Help.class));
//                finish();
//
//            }
//        });


//        btnToTips.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(AfterSignUpFragment.this, HelpFragment.class));
//                finish();
//            }
//        });


    }
}
