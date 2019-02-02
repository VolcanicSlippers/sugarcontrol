package com.karimtimer.sugarcontrol.Reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.karimtimer.sugarcontrol.R;

import java.text.DateFormat;
import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class Reminder extends android.support.v4.app.Fragment {

    private NotificationManagerCompat notificationManager;
    private TextView timeView;
    private EditText editTextMessage;
    private Button btn1, btnCancel;
    private Toolbar toolbar;
    private int emonth, eday, eyear;
    private AlarmManager alarmManager;
    private Button btnDatePicker, btnTimePicker;
    private int mYear;
    private int mMonth;
    private int mDay;


    private int mHour;
    private int mMinute;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_notification, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        notificationManager = NotificationManagerCompat.from(getActivity());
        btnDatePicker = (Button) view.findViewById(R.id.btn_date_notification);
        btnTimePicker = (Button) view.findViewById(R.id.btn_time_notification);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        timeView = view.findViewById(R.id.reminder_txt_view);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

        btn1 = view.findViewById(R.id.btn_send_channel1);


        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnDatePicker.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear + 1), year));

                                setEday(dayOfMonth);
                                setEmonth(monthOfYear + 1);
                                setEyear(year);
                            }
                        }, mYear, mMonth, mDay);


                Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
                datePickerDialog.show();

            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar cTime = Calendar.getInstance();
                mHour = cTime.get(Calendar.HOUR_OF_DAY);
                mMinute = cTime.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                btnTimePicker.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!btnTimePicker.getText().toString().equals("Select Time")) {
                    if (!btnDatePicker.getText().toString().equals("Select Date")) {
                        String fullTxt = btnTimePicker.getText().toString();
                        String[] syllables = fullTxt.split(":");
                        String hourString = syllables[0];
                        String minuteString = syllables[1];


                        String fullTxtDate = btnDatePicker.getText().toString();
                        String[] dateSplit = fullTxtDate.split("/");
                        String dayString = dateSplit[0];
                        String monthString = dateSplit[1];
                        String yearString = dateSplit[2];

                        int hoursGood = Integer.parseInt(hourString);
                        int minutesGood = Integer.parseInt(minuteString);
                        int daysGood = Integer.parseInt(dayString);
                        int monthsGood = Integer.parseInt(monthString) - 1;
                        int yearsGood = Integer.parseInt(yearString);


                        Log.e(TAG, hoursGood + " jake " + minutesGood);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hoursGood);
                        c.set(Calendar.MINUTE, minutesGood);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.DAY_OF_MONTH, daysGood);
                        c.set(Calendar.MONTH, monthsGood);
                        c.set(Calendar.YEAR, yearsGood);
                        String message = "reminder";





                        updateTimeText(c);
                        startAlarm(c);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"Date not selected.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Time not selected.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), App.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        timeView.setText("Reminder cancelled for today.");
    }

    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private void updateTimeText(Calendar c) {
        String timeText = "Great!\nReminder set for:";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime())+ "!";

        timeView.setText(timeText);
    }


    public void sendOnChannel1(View v) {


    }
//    public void sendOnChannel2(View v){
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();
//
//        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
//                .setSmallIcon(R.drawable.icon2)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//
//        notificationManager.notify(2,notification);
//
//    }


    private int getEmonth() {
        return emonth;
    }

    private void setEmonth(int emonth) {
        this.emonth = emonth;
    }

    private int getEday() {
        return eday;
    }

    public void setEday(int eday) {
        this.eday = eday;
    }

    private int getEyear() {
        return eyear;
    }

    private void setEyear(int eyear) {
        this.eyear = eyear;
    }


    public int getmHour() {
        return mHour;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    public int getmMinute() {
        return mMinute;
    }

    public void setmMinute(int mMinute) {
        this.mMinute = mMinute;
    }

}