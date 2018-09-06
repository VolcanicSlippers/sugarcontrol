package com.karimtimer.sugarcontrol.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

public class Tour5  extends android.support.v4.app.Fragment{

    private Button next, back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.tour_fragment_5, parent, false);
        // view.setBackgroundColor(GradientDrawable { getResources().getColor(Constant.color), Color.WHITE, Color.WHITE });
        return view;
    }





    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        next = view.findViewById(R.id.btn_tour_5);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });


    }
}
