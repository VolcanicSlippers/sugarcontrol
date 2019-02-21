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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private DatabaseReference myRef, refToFirstName, refToLastName, refToDiabetesType, refToHeight, refToWeight, myRefForSettings;
    private FirebaseDatabase mFirebaseDatabase;
    private Spinner spinnerChangeDiabetesType;
    private String firstName;
    private String lastName;
    private String weight;
    private String height;


    private String diabetesType;



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
        refToDiabetesType = mFirebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("type");

        // Spinner element for diabetes type
        spinnerChangeDiabetesType = view.findViewById(R.id.spinner_change_diabetes_type);

       //TODO:fix the spinner over here
        final List<String> categoriesDiabetes = new ArrayList<String>();
        categoriesDiabetes.add("Prediabetes");
        categoriesDiabetes.add("Type 1");
        categoriesDiabetes.add("Type 2");
        categoriesDiabetes.add("Gestational");
        categoriesDiabetes.add("LADA");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriesDiabetes);

        spinnerChangeDiabetesType.setAdapter(dataAdapter);

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

        //DiabetesType
        refToDiabetesType.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                Log.e(TAG, type);
                setDiabetesType(type);

                spinnerChangeDiabetesType.setSelection(categoriesDiabetes.indexOf(getDiabetesType()));
                spinnerChangeDiabetesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // On selecting a spinner item
                        String item = parent.getItemAtPosition(position).toString();


                        // Showing selected spinner item
                        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });

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

        //TODO: save the diabetes type
        //update the first and last name
        btnSavePersonalSettings.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");

                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = mCurrentUser.getUid();
                    final String firstName = getFirstName();
                    final String lastName = getLastName();

                                @SuppressWarnings("VisibleForTests") final DatabaseReference newSettingChanges = mFirebaseDatabase.getReference().child("users");
                                Map<String, Object> update = new HashMap<>();
                                update.put(mAuth.getUid()+"/firstName", getFirstName());

                                update.put(mAuth.getUid()+"/lastName", getLastName());

                                update.put(mAuth.getUid()+"/type", getDiabetesType());


                                newSettingChanges.updateChildren(update);

                                //post the stuff
                                Log.e(TAG, "saved the fields!");
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                Toast.makeText(getActivity(), " " + getFirstName()+", "+ getLastName(), Toast.LENGTH_SHORT).show();
                }
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

    public String getDiabetesType() {
        return diabetesType;
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }


}