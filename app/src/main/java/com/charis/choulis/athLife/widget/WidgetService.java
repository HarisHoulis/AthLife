package com.charis.choulis.athLife.widget;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.charis.choulis.athLife.C;
import com.charis.choulis.athLife.R;

public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && C.ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            boolean showEvents = intent.getBooleanExtra(C.EXTRA_SHOWEVENTS, true);
            String category = intent.getStringExtra(C.EXTRA_CATEGORY);
            updateWidgets(showEvents, category);
        }
    }

    public static void startActionUpdateWidget(Context context, boolean showEvents, String category, int appwidgetId) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(C.ACTION_UPDATE_WIDGET);
        intent.putExtra(C.EXTRA_APPWIDGET_ID, appwidgetId);
        intent.putExtra(C.EXTRA_SHOWEVENTS, showEvents);
        intent.putExtra(C.EXTRA_CATEGORY, category);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notif_channel_name";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("yy")
                .setContentText("nn")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return mBuilder.build();
    }

    private void updateWidgets(boolean showEvents, String category) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, AppWidget.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list);
        //Now update all widgets
        AppWidget.updateAppWidgets(this, appWidgetManager, showEvents, category, appWidgetIds);
    }
}
