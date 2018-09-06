package com.karimtimer.sugarcontrol.Reminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Record.RecordActivity;

import static com.karimtimer.sugarcontrol.Reminder.App.CHANNEL_1_ID;

public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message= intent.getStringExtra("toastMessage");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        notificationManager = NotificationManagerCompat.from(context);

        Intent activityIntent = new Intent(context, RecordActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,activityIntent,0);

        Intent broadCastIntent = new Intent(context,NotificationReceiver.class);
        broadCastIntent.putExtra("toastMesssage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,0,broadCastIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_action_insulina_vers_5xxxhdpi)
                .setContentTitle("Reminder from Insulina")
                .setContentText("Time to record your blood glucose level!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.YELLOW)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_action_insulina_vers_5xxxhdpi,"Record", actionIntent)
                .build();


        notificationManager.notify(1, notification);

    }
}
