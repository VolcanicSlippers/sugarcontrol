package com.karimtimer.sugarcontrol.Treatment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.R;

public class MedicationMainFragment extends Fragment {

    private Button addMedication;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.medication_main_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addMedication = (Button) view.findViewById(R.id.add_treatment);



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Medication").child(mAuth.getUid());
        mDatabase.keepSynced(true);



        addMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //once clicked, we want to check if the user has a medication type. The types being insulin taker, medication taker, or both.
                if(mDatabase == null){
                    // allow user to input user medication type.

                }


            }
        });



    }

}
