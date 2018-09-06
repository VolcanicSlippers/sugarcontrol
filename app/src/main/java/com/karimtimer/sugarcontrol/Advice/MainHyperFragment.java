package com.karimtimer.sugarcontrol.Advice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.karimtimer.sugarcontrol.Statistics.ExpandableListAdapter;
import com.karimtimer.sugarcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainHyperFragment extends Fragment {
    private static final String TAG = "Main Hyper advice fragment";

    private ExpandableListView listView2;
    private ExpandableListAdapter listAdapter2;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.advice_hyper_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView2 = (ExpandableListView) view.findViewById(R.id.expandableList_hyper);
        initData();

        listAdapter2 = new ExpandableListAdapter(getActivity(), listDataHeader, listHash);
        listView2.setAdapter(listAdapter2);

    }



    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

//        listDataHeader.add(getString(R.string.main_screen_report_title)); //daily header
        listDataHeader.add("What is a hyper?"); //weekly header
        listDataHeader.add("What are some signs of having high blood sugars?");//monthly header
        listDataHeader.add("How can I treat one?");//monthly header

//        List<String> dailerHeader = new ArrayList<>();
//        dailerHeader.add(getString(R.string.number_of_entries));
//        dailerHeader.add(getString(R.string.entries_in_range));
//        dailerHeader.add(getString(R.string.entries_below_range));
//        dailerHeader.add(getString(R.string.entries_above_range));
//        dailerHeader.add(getString(R.string.avg_blood_glucose_level));
//        dailerHeader.add(getString(R.string.glucose_max));
//        dailerHeader.add(getString(R.string.glucose_min));
//        dailerHeader.add(getString(R.string.standard_deviation));

        List<String> weeklyHeader = new ArrayList<>();
        weeklyHeader.add(getString(R.string.whatIsHyper));

        List<String> weeklyHeader2 = new ArrayList<>();
        weeklyHeader2.add(getString(R.string.signsofhyper));
        List<String> weeklyHeader3 = new ArrayList<>();
        weeklyHeader3.add(getString(R.string.whatdohyper));


//        listHash.put(listDataHeader.get(0), dailerHeader);
        listHash.put(listDataHeader.get(0), weeklyHeader);
        listHash.put(listDataHeader.get(1), weeklyHeader3);;
        listHash.put(listDataHeader.get(2), weeklyHeader2);
//        listHash.put(listDataHeader.get(1), weeklyHeader2);

    }
}
