package com.karimtimer.sugarcontrol.userAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

public class Help extends AppCompatActivity {

    private Button btnToHome;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_tour);

        //Toolbar for saving items
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        btnToHome = findViewById(R.id.btn_return_home);

        btnToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Help.this, MainActivity.class));
                finish();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Help.this, MainActivity.class));

        finish();
    }
}
