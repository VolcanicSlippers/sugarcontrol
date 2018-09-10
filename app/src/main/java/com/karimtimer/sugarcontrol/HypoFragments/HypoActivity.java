package com.karimtimer.sugarcontrol.HypoFragments;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.karimtimer.sugarcontrol.HypoFragments.*;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.SectionsPageAdapater;

/**
 * @author Abdikariim Timer
 */
public class HypoActivity extends AppCompatActivity {


    private static final String TAG = "GraphActivity";

    private SectionsPageAdapater mSectionsPageAdapter;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hypo);

        mSectionsPageAdapter = new SectionsPageAdapater(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPage(mViewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPage(ViewPager viewPager) {
        SectionsPageAdapater adapter = new SectionsPageAdapater(getSupportFragmentManager());
        adapter.addFragment(new HypoFragment1(), "Emergency Contact");
        adapter.addFragment(new HypoFragment2(), "How should I treat a Hypo?");
        viewPager.setAdapter(adapter);
    }
}