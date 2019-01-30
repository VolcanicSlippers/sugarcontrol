package com.karimtimer.sugarcontrol.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karimtimer.sugarcontrol.Advice.AdviceActivity;
import com.karimtimer.sugarcontrol.Bluetooth.Bluetooth;
import com.karimtimer.sugarcontrol.Bluetooth.BluetoothBgl;
import com.karimtimer.sugarcontrol.Insulina.ClickListener;
import com.karimtimer.sugarcontrol.Emergency.MainEmergency;
import com.karimtimer.sugarcontrol.Insulina.MainBotFragment;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Reminder.Reminder;
import com.karimtimer.sugarcontrol.Tour.TourActivity;
import com.karimtimer.sugarcontrol.userAccount.Options;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    SharedPreferences sharedPreferences;
    private NavigationView mView;
    private TextView txtViewEmail, txtName;
    private View mHeaderView;

    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(Constant.color);
        // Find our drawer view
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mHeaderView= navigationView.getHeaderView(0);

        txtViewEmail = (TextView) mHeaderView.findViewById(R.id.nav_header_user_email);
        txtName = (TextView) mHeaderView.findViewById(R.id.nav_header_user_first_name_last_name);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        txtViewEmail.setText(mCurrentUser.getEmail());
        txtName.setText("Welcome "+mCurrentUser.getDisplayName() + "!");
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ClickListener.HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ClickListener.HomeFragment()).commit();
                break;
            case R.id.nav_insulina:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainBotFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Options()).commit();
                break;
            case R.id.nav_hypo:
                startActivity(new Intent(MainActivity.this, MainEmergency.class));
                finish();
                break;
            case R.id.nav_reminder:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Reminder()).commit();
                break;
            case R.id.nav_advice:
                startActivity(new Intent(MainActivity.this, AdviceActivity.class));
                finish();
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this, TourActivity.class));
                finish();
                break;
            case R.id.nav_bluetooth:
                if(mCurrentUser.getDisplayName().equals("bglDevice01")){
                    startActivity(new Intent(MainActivity.this, BluetoothBgl.class));
                    finish();
                }else{
                    startActivity(new Intent(MainActivity.this, Bluetooth.class));
                    finish();
                }

                break;
            case R.id.nav_logout:
                signOut();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //sign out method
    public void signOut() {
        mAuth.signOut();
        finish();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}