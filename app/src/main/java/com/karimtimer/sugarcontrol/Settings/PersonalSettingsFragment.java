package com.karimtimer.sugarcontrol.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karimtimer.sugarcontrol.R;

public class PersonalSettingsFragment extends Fragment {

    private static final String TAG = "Personal settings fragment";

    private TextView txtFirstName, txtLastName, txtHeight, txtWeight;
    //for connecting to the database, and recieving user information
    private DatabaseReference mDatabaseCurrentRecord, mDatabaseRange;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private StorageReference storage;
    private DatabaseReference myRef, refToFirstName, refToLastName, refToHeight, refToWeight;
    private FirebaseDatabase mFirebaseDatabase;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    private String firstName, lastName, weight, height;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings_personal, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtFirstName = view.findViewById(R.id.field_first_name_change);
        txtLastName = view.findViewById(R.id.field_last_name_change);

        //firebase initialising stuff
        storage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        refToFirstName = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("firstName");
        refToLastName = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("lastName");
        refToHeight = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("height");
        refToWeight = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("weight");

        //first name
        refToFirstName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.getValue(String.class);
                setFirstName(firstName);
                txtFirstName.setText(getFirstName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //last name
        refToLastName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastName = dataSnapshot.getValue(String.class);
                setLastName(lastName);
                txtLastName.setText(getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //height
//        refToHeight.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String height = dataSnapshot.getValue(String.class);
//                setHeight(height);
//                txtHeight.setText(getHeight());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        //weight
//        refToWeight.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String weight = dataSnapshot.getValue(String.class);
//                setWeight(weight);
//                txtWeight.setText(getWeight());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
}
}