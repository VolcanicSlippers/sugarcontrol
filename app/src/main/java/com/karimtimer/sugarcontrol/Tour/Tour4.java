package com.karimtimer.sugarcontrol.Tour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.karimtimer.sugarcontrol.R;

public class Tour4  extends android.support.v4.app.Fragment{

    private Button next, back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.tour_fragment_4, parent, false);
        // view.setBackgroundColor(GradientDrawable { getResources().getColor(Constant.color), Color.WHITE, Color.WHITE });
        return view;
    }





    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        next = view.findViewById(R.id.btn_tour_4);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment tourFragment = new Tour5();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_tour, tourFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }
}
