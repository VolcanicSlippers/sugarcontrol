package com.karimtimer.sugarcontrol.History;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.models.Record;

import java.util.ArrayList;

/**
 * @author Abdikariim Timer
 */
public class ViewRecords extends AppCompatActivity{
    private static final String TAG = "ViewDatabase";
    private Record record;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private Toolbar toolbar;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_record_database);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


      //  mListView = (ListView) findViewById(R.id.record_list_view);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user signed in
                //    Log.d(TAG,"onAuthStateChaned:signed_in:" + user.getUid());
               //     toastMessage("Successfully signed in with: " + user.getEmail());
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showData(DataSnapshot dataSnapshot){

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            //declare record object
           // Record record = new Record();
           // record.setNotes(ds.child(userID).getValue(Record.class).getNotes());//set notes

            Record uInfo = new Record();

            uInfo.setSugarLevel(ds.getValue(Record.class).getSugarLevel()); //set the email
            uInfo.setNotes(ds.getValue(Record.class).getNotes()); //set the email
            uInfo.setCarbs(ds.getValue(Record.class).getCarbs()); //set the email
            uInfo.setTime(ds.getValue(Record.class).getTime()); //set the email
            uInfo.setDate(ds.getValue(Record.class).getDate()); //set the email


            //display all info from records
            Log.d(TAG, "showData: name: "+ uInfo.getSugarLevel());


            ArrayList<String> array = new ArrayList<>();
            //array.add(record.getNotes());
            array.add(uInfo.getSugarLevel());
            array.add(uInfo.getNotes());
            array.add(uInfo.getCarbs());
            array.add(uInfo.getDate());
            array.add(uInfo.getTime());
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            mListView.setAdapter(adapter);

        }

    }




    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
