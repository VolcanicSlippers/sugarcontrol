package com.karimtimer.sugarcontrol.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.SectionsPageAdapater;

public class SettingsMainActivity  extends AppCompatActivity {


    private static final String TAG = "Settings activity";

    private SectionsPageAdapater mSectionsPageAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_activity);

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setBackgroundColor(Constant.color);


        mSectionsPageAdapter = new SectionsPageAdapater(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        setupViewPage(mViewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPage(ViewPager viewPager) {
        SectionsPageAdapater adapter = new SectionsPageAdapater(getSupportFragmentManager());
        adapter.addFragment(new PersonalSettingsFragment(), "Personal");
        adapter.addFragment(new GeneralSettingsFragment(), "General");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsMainActivity.this, MainActivity.class));
        finish();
    }
}
