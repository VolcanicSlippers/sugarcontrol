package com.karimtimer.sugarcontrol.Record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.karimtimer.sugarcontrol.R;



public class MainRecordActivity extends AppCompatActivity{

    private Button btn1, btn2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_record);

    btn1 = (Button) findViewById(R.id.record_main_btn_record_bgl);
    btn2 = (Button) findViewById(R.id.record_main_btn_record_nfc);

    btn1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    });

    btn2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    });


    }
}
