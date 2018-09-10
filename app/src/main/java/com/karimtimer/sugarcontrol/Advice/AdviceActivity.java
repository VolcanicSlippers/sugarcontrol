package com.karimtimer.sugarcontrol.Advice;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.SectionsPageAdapater;

/**
 * @author Abdikariim Timer
 * This class contains the code for the Activity screen that contains the code for the different Advice fragments.
 * The user may swap between the the following three fragments:
 * -Hyper Fragment: where the user views information about having high blood glucose lvls
 * -Hypo Fragment: where the user views information about having low blood glucose lvls
 * -Main Fragment: where the user views information about general diabetes support.
 */
public class AdviceActivity extends AppCompatActivity {


    private static final String TAG = "Advice activity";

    private SectionsPageAdapater mSectionsPageAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_activity);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setBackgroundColor(Constant.color);


        mSectionsPageAdapter = new SectionsPageAdapater(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPage(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPage(ViewPager viewPager) {
        SectionsPageAdapater adapter = new SectionsPageAdapater(getSupportFragmentManager());
        adapter.addFragment(new MainHypoFragment(), "Hypo\n Advice");
        adapter.addFragment(new GeneralAdviceFragment(), "General Advice");
        adapter.addFragment(new MainHyperFragment(), "Hyper\n Advice");
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
        startActivity(new Intent(AdviceActivity.this, MainActivity.class));
        finish();
    }
}
