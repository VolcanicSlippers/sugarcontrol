package com.karimtimer.sugarcontrol.userAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.karimtimer.sugarcontrol.Main.MainActivity;

public class Report extends Fragment {



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private int convertStringToInt(String word){

        return Integer.parseInt(word);
    }
    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

}



