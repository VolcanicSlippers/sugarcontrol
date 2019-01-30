package com.karimtimer.sugarcontrol.Insulina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karimtimer.sugarcontrol.History.HistoryActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Record.RecordActivity;
import com.karimtimer.sugarcontrol.Record.RecordHba1c;
import com.karimtimer.sugarcontrol.Statistics.GraphActivity;
import com.karimtimer.sugarcontrol.Treatment.MedicationChooseType;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);

    class HomeFragment extends android.support.v4.app.Fragment {

        private ImageView recordBtnBgl, recordBtnMedicine, recordBtnHbA1c, btnStats, btnHistory;
        private TextView txtRecordBgl, txtRecordMeds, txtRecordHba1c, txtInsulina;

        //for connecting to the database, and recieving user information
        private DatabaseReference mDatabaseCurrentRecord, mDatabaseRange;
        private FirebaseUser mCurrentUser;
        private FirebaseAuth mAuth;
        private FirebaseDatabase database, mFirebaseDatabase;
        private DatabaseReference databaseRef1, databaseRef2;
        private DatabaseReference myRef, myRef2, myRef3;
        private ImageView recordBtn;
        private ImageView insulinaHome;
        private boolean isRotated;
        private boolean isFirsTime;
        private boolean isThereABgl;
        private String latestReading;
        private int upperRange, lowerRange;
        private double daySugarLevelReadingInDouble;
        private TextView txthistory, txtstatistics;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.first_fragment, parent, false);
            setFirsTime(true);
            return view;
        }





        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {


            final ViewGroup transitionsContainer = (ViewGroup) view.findViewById(R.id.fragment_home_container);
            recordBtn = (ImageView) view.findViewById(R.id.btn_record);
            recordBtnBgl = (ImageView) view.findViewById(R.id.btn_record_bgl);
            recordBtnHbA1c = (ImageView) view.findViewById(R.id.btn_record_HbA1c);
            recordBtnMedicine = (ImageView) view.findViewById(R.id.btn_record_medicine);

            btnHistory = (ImageView) view.findViewById(R.id.btn_history);
            btnStats = (ImageView) view.findViewById(R.id.btn_graph);

            txthistory = view.findViewById(R.id.txt_main_history);
            txtstatistics = view.findViewById(R.id.txt_main_stats);



            txtRecordBgl = view.findViewById(R.id.txt_record_bgl);
            txtRecordMeds = view.findViewById(R.id.txt_record_medicine);
            txtRecordHba1c = view.findViewById(R.id.txt_record_hba1c);
            txtInsulina = (TextView) view.findViewById(R.id.txt_main_screen_insulina_remark);


            recordBtnBgl.setVisibility(View.INVISIBLE);
            recordBtnMedicine.setVisibility(View.INVISIBLE);
            recordBtnHbA1c.setVisibility(View.INVISIBLE);
            txtRecordHba1c.setVisibility(View.INVISIBLE);
            txtRecordMeds.setVisibility(View.INVISIBLE);
            txtRecordBgl.setVisibility(View.INVISIBLE);


            transitionsContainer.findViewById(R.id.btn_record).setOnClickListener(new VisibleToggleClickListener() {


                @Override
                protected void changeVisibility(boolean visible) {
                    TransitionSet set = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() :
                                    new FastOutLinearInInterpolator());

                    TransitionManager.beginDelayedTransition(transitionsContainer, set);
                    TransitionManager.beginDelayedTransition(transitionsContainer, new Recolor());
                    if(isRotated() == false) {
                        Log.e(TAG, "HERE!");
                        RotateAnimation rotate = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(250);
                        rotate.setInterpolator(new LinearInterpolator());

                       // setRotated(true);
                        recordBtn.startAnimation(rotate);
                        rotate.setFillAfter(true);
                    }if(isRotated() == true){
                        Log.e(TAG, "here?");
                        RotateAnimation rotate = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(250);
                        rotate.setInterpolator(new LinearInterpolator());

                      //  setRotated(false);
                        recordBtn.startAnimation(rotate);
                        rotate.setFillAfter(true);
                    }
                    if(isRotated() ==true) setRotated(false);
                    else if(isRotated() ==false) setRotated(true);
                    recordBtn.setColorFilter(getResources().getColor(!isRotated() ? R.color.colorPrimaryDark :R.color.primary_text));


                    recordBtnBgl.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    recordBtnHbA1c.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    recordBtnMedicine.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    txtRecordBgl.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    txtRecordMeds.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    txtRecordHba1c.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }

            });



            recordBtnBgl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(), RecordActivity.class));
                    getActivity().finish();
                }
            });
            recordBtnMedicine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(), MedicationChooseType.class));
                    getActivity().finish();
                }
            });
            recordBtnHbA1c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), RecordHba1c.class));
                    getActivity().finish();

                }
            });



            btnStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnStats.setColorFilter(getResources().getColor(R.color.colorAccent));
                    startActivity(new Intent(getActivity(), GraphActivity.class));
                    getActivity().finish();
                }
            });
            btnHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnHistory.setColorFilter(getResources().getColor(R.color.colorAccent));

                    startActivity(new Intent(getActivity(), HistoryActivity.class));
                    getActivity().finish();
                }
            });

            insulinaHome = (ImageView) view.findViewById(R.id.insulina_main_page);

            insulinaHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment insulinaFragment = new MainBotFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, insulinaFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


            //firebase stuff
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
            myRef2 = mFirebaseDatabase.getReference().child("Current Record").child("SugarLevel").child(mAuth.getUid());
            myRef3 = mFirebaseDatabase.getReference().child("Range").child("SugarLevel").child(mAuth.getUid());
            mDatabaseCurrentRecord = FirebaseDatabase.getInstance().getReference().child("Current Record").child("SugarLevel").child(mAuth.getUid());

            //setting the gauge value to latest sugar reading
            mDatabaseCurrentRecord.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.e(TAG, "lmao" + dataSnapshot.child("Recording").getValue());
            if(dataSnapshot.child("Recording").exists()) {
                String daySugarLevelReading = dataSnapshot.child("Recording").getValue().toString();
                Log.e(TAG, "the bgl: "+ daySugarLevelReading);
                setThereABgl(true);
                setLatestReading(daySugarLevelReading);
                setDaySugarLevelReadingInDouble( Double.parseDouble(daySugarLevelReading));
                float daySugarLevelReadingNum = (float) Double.parseDouble(daySugarLevelReading);

                String dateOfReadingAvg = dataSnapshot.child("date").getValue().toString();

                Date date = Calendar.getInstance().getTime();

                // Display a date in day, month, year format
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String today = formatter.format(date);

                Log.e(TAG, today + ",,,,,,,,,,," + dateOfReadingAvg);

            }

                    insulinaRemarker(isFirsTime());

                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }

            });


        }

        private void insulinaRemarker(boolean firsTime) {
            double currentBglReading =  getDaySugarLevelReadingInDouble();
            if(isFirsTime ==true){
                if(isThereABgl() == false){
                    Log.e(TAG, "is it their first time? "+isThereABgl +", and the bgl is: "+ getLatestReading());
                    txtInsulina.setText("hey " + mCurrentUser.getDisplayName()+ "! It looks like you haven't got a reading for today... Make one!");
                }else{
                    if(currentBglReading < 4){
                        txtInsulina.setText(getString(R.string.insulina_low));
                    }else if(currentBglReading >10){
                        txtInsulina.setText(getString(R.string.insulina_high));

                    }else {
                        txtInsulina.setText(getString(R.string.insulina_normal)+ getLatestReading() + getString(R.string.insulina_normal_2));

                    }
                    }
            }
        }


    public boolean isRotated() {
        return isRotated;
    }

        public void setRotated(boolean rotated) {
            isRotated = rotated;
        }


        public boolean isFirsTime() {
            return isFirsTime;
        }

        public void setFirsTime(boolean firsTime) {
            isFirsTime = firsTime;
        }

        public String getLatestReading() {
            return latestReading;
        }

        public void setLatestReading(String latestReading) {
            this.latestReading = latestReading;
        }
        public boolean isThereABgl() {
            return isThereABgl;
        }

        public void setThereABgl(boolean thereABgl) {
            isThereABgl = thereABgl;
        }
        public double getDaySugarLevelReadingInDouble() {
            return daySugarLevelReadingInDouble;
        }

        public void setDaySugarLevelReadingInDouble(double daySugarLevelReadingInDouble) {
            this.daySugarLevelReadingInDouble = daySugarLevelReadingInDouble;
        }

    }
}