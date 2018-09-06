package com.karimtimer.sugarcontrol.Tour;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karimtimer.sugarcontrol.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
this.context = context;

    }

    //Arrays
    public int[] slide_images = {
            R.drawable.insulina_vers_5hdpi,
            R.drawable.ic_reminder,
            R.drawable.ic_translate_black_24dp,
            R.drawable.icon_plusxhdpi


    };

    public String[] slide_headings = {
      "Speak to Insulina",
      "Reminder Yourself",
      "Different Languages",
      "Take a Recording"

    };

    public String[] slide_descs ={

      "Speak to Insulina, by clicking on her icon!",
      "Remind yourself to take a recording. Click on the burger icon on the top left to  find reminders, and other funcitonalities",
      "Sugar Control allows you to change languages, check the offerings by going to settings.",
      "to take a blood glucose, medicational or even hba1c recording, click on the icon above."

    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.text_title);
        TextView slideDescription = (TextView) view.findViewById(R.id.text_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);


    }
}
