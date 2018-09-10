package com.karimtimer.sugarcontrol.Emergency;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.karimtimer.sugarcontrol.Emergency.MainEmergency;
import com.karimtimer.sugarcontrol.R;

/**
 * @author Abdikariim Timer
 */
public class EmergencyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds){
            //here we can decide what we can do with this widget

            Intent intent = new Intent(context, MainEmergency.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,0 );

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.emergency_widget);

            views.setOnClickPendingIntent(R.id.emergency_widget_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }
}
