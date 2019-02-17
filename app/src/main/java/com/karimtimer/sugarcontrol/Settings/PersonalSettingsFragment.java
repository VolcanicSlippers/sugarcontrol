package com.karimtimer.sugarcontrol.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Record.RecordActivity;

import java.util.HashMap;
import java.util.Map;

public class PersonalSettingsFragment extends Fragment {

    private static final String TAG = "Personal settings fragment";

    //TODO: create security checks or checks of some kind for the fields being allowed through
    private TextView txtFirstName, txtLastName, txtHeight, txtWeight;
    private Button btnSavePersonalSettings;
    //for connecting to the database, and recieving user information
    private DatabaseReference mDatabaseCurrentRecord, mDatabaseRange;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private StorageReference storage;
    private DatabaseReference myRef, refToFirstName, refToLastName, refToHeight, refToWeight, myRefForSettings;
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

        txtFirstName.addTextChangedListener(filterTextWatcher);
        txtLastName.addTextChangedListener(filterTextWatcher2);


        btnSavePersonalSettings = view.findViewById(R.id.btn_save_personal_settings);

        //firebase initialising stuff
        storage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        myRefForSettings = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid());
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

//        //weight
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

        //update the first and last name
        btnSavePersonalSettings.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");

                //if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    //    String recordNumber = counter;
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = mCurrentUser.getUid();
                    final String firstName = getFirstName();
                    final String lastName = getLastName();

//                    if (getFirstName().equals()) {
//                        if (!date.equals("Select Date")) {
//                            if (!sugarLevel.equals("")) {

                                @SuppressWarnings("VisibleForTests") final DatabaseReference newSettingChanges = mFirebaseDatabase.getReference().child("users");
//
                                Map<String, Object> update = new HashMap<>();
                                update.put(mAuth.getUid()+"/firstName", getFirstName());
                                //newSettingChanges.updateChildren(update);

                                update.put(mAuth.getUid()+"/lastName", getLastName());

                                newSettingChanges.updateChildren(update);
//                              newSettingChanges.child("firstName").setValue(getFirstName());
//                              newSettingChanges.child("lastName").setValue(getLastName());

                                //post the stuff
                                Log.e(TAG, "saved the fields!");
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                mCurrentUser.
                                Toast.makeText(getActivity(), " " + getFirstName()+", "+ getLastName(), Toast.LENGTH_SHORT).show();



//                            } else {
//                                Toast.makeText(RecordActivity.this, "Enter a Blood Glucose reading!", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//                            Toast.makeText(RecordActivity.this, "Enter a valid date.", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } else {
//                        Toast.makeText(RecordActivity.this, "Enter a valid time.", Toast.LENGTH_SHORT).show();
//
//                    }

                }
               // mLastClickTime = SystemClock.elapsedRealtime();
          //  }

            //   }
        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        /**
         * Method used to both transition the previous
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
            if(!editable.toString().isEmpty()) {
                String firstNameBeingSaved = editable.toString();
                setFirstName(firstNameBeingSaved);
            }
        }
    };
    private TextWatcher filterTextWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        /**
         * Method used to both transition the previous
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
            if(!editable.toString().isEmpty()) {
                String lastNameBeingSaved = editable.toString();
                setLastName(lastNameBeingSaved);
            }
        }
    };

}