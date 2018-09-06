package com.karimtimer.sugarcontrol.Reminder;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "reminder1"; //chane name to what it is for
    public static final String  CHANNEL_2_ID = "channel2";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){
        //here, we're checking if we are in the correct version of Android. Oreo has diff stuff.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.enableVibration(true);
            channel1.setDescription("This is the first reminder");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }


}
