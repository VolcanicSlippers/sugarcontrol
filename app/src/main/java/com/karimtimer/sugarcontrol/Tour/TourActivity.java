package com.karimtimer.sugarcontrol.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

public class TourActivity extends AppCompatActivity{


    private ViewPager mSlideViewPage;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button mNextButton, mBackBtn;
    private int intCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_activity);


        mSlideViewPage = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPage.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mNextButton = (Button) findViewById(R.id.nextBtn);
        mBackBtn = (Button) findViewById(R.id.previousBtn);

        mSlideViewPage.addOnPageChangeListener(viewListener);


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intCurrentPage + 1 == 4) {
                    startActivity(new Intent(TourActivity.this, MainActivity.class));
                    finish();
                } else {
                    mSlideViewPage.setCurrentItem(intCurrentPage + 1);
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPage.setCurrentItem(intCurrentPage-1);
            }
        });

    }

    public void  addDotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length ; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.grey));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }


    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            intCurrentPage = position;

            if(position ==0){
                mNextButton.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextButton.setText("Next");
                mBackBtn.setText("");

            }else if(position == mDots.length-1){
                mNextButton.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextButton.setText("Finish");
                mBackBtn.setText("Back");
            }else{
                mNextButton.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextButton.setText("Next");
                mBackBtn.setText("Back");

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TourActivity.this, MainActivity.class));

        finish();
    }
}
