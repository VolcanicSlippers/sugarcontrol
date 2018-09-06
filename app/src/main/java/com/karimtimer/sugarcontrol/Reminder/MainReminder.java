package com.karimtimer.sugarcontrol.Reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.karimtimer.sugarcontrol.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.support.constraint.Constraints.TAG;

public class MainReminder extends  android.support.v4.app.Fragment {

    private NotificationManagerCompat notificationManager;
//    private EditText editTextTitle;
    private EditText editTextMessage;
    private Button btn1;
    private Toolbar toolbar;
    private int emonth, eday, eyear;
    private AlarmManager alarmManager;
    private Button btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;


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




//        String tiime = btnTimePicker.getText().toString();
//        int time = Integer.parseInt(tiime);
//         String daate = btnDatePicker.getText().toString();
//         int date = Integer.parseInt(daate);
//        int timeOfReminder = time+date;
//
//        String source = tiime;
//        String[] tokens = source.split(":");
//        int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
//        int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
//        long total =minutesToMs + hoursToMs;



//        editTextTitle = view.findViewById(R.id.edit_text_title);
        //editTextMessage = view.findViewById(R.id.edit_text_message);
        btn1 = view.findViewById(R.id.btn_send_channel1);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == btnDatePicker) {

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

                                    btnDatePicker.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear+1), year));

                                    setEday(dayOfMonth);
                                    setEmonth(monthOfYear + 1);
                                    setEyear(year);
                                }
                            }, mYear, mMonth, mDay);


                    Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
                    datePickerDialog.show();
                }
                if (v == btnTimePicker) {

                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    btnTimePicker.setText(String.format("%02d:%02d", hourOfDay, minute));                        }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }

            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == btnDatePicker) {

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

                                    btnDatePicker.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear+1), year));

                                    setEday(dayOfMonth);
                                    setEmonth(monthOfYear + 1);
                                    setEyear(year);
                                }
                            }, mYear, mMonth, mDay);


                    Log.w(TAG, "month is: " + getEday() + ", " + getEmonth() + ", " + getEyear());
                    datePickerDialog.show();
                }
                if (v == btnTimePicker) {

                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    btnTimePicker.setText(String.format("%02d:%02d", hourOfDay, minute));                        }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alarmService
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                Calendar calCal = Calendar.getInstance();
                calCal.add(Calendar.SECOND, 5);
//                calCal.set(Calendar.HOUR_OF_DAY, mHour);
//                calCal.set(Calendar.MINUTE, mMinute);

                Intent intent = new Intent(".action.DISPLAY_NOTIFICATION");

                PendingIntent broadcast =  PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calCal.getTimeInMillis(), broadcast);
                Log.e(TAG, "any minuite now..");
            }
        });

    }


//    public void sendOnChannel1(View v) {
//
//        String title = editTextTitle.getText().toString();
//        // String message = editTextMessage.getText().toString();
//
//        Intent activityIntent = new Intent(getActivity(), RecordActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),0,activityIntent,0);
//
//        String tiime = btnTimePicker.getText().toString();
////        int time = Integer.parseInt(tiime);
//        // String daate = btnDatePicker.getText().toString();
//        // int date = Integer.parseInt(daate);
//        //int timeOfReminder = time+date;
//
//        String source = tiime;
//        String[] tokens = source.split(":");
//        int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
//        int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
//        long total =minutesToMs + hoursToMs;
//
//
//
//    }
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



}
