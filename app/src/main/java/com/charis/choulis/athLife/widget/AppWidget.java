package com.charis.choulis.athLife.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.charis.choulis.athLife.C;
import com.charis.choulis.athLife.H;
import com.charis.choulis.athLife.R;
import com.charis.choulis.athLife.data.DataProvider;
import com.charis.choulis.athLife.ui.activities.SplashActivity;

public class AppWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        boolean showEvents, String category, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        if (H.isNetworkAvailable(context) || areCachedData(showEvents, category)) {
            views = handleNonEmptySource(views, context, showEvents, category, appWidgetId);
        } else {
            views = handleEmptySource(views, context, showEvents, category, appWidgetId);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, boolean showEvents, String category, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, showEvents, category, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            String category = AppWidgetConfigureActivity.loadCategoryPref(context, appWidgetId);
            boolean showEvents = AppWidgetConfigureActivity.loadShowEventsPref(context, appWidgetId);
            WidgetService.startActionUpdateWidget(context, showEvents, category, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    private static RemoteViews handleNonEmptySource(RemoteViews views, Context context, boolean showEvents, String category, int appWidgetId) {
        views.setViewVisibility(R.id.appwidget_error_ll, View.GONE);
        views.setViewVisibility(R.id.appwidget_title_ll, View.VISIBLE);
        views.setViewVisibility(R.id.appwidget_list_rl, View.VISIBLE);

        // Set the intent for the RemoteViewsService
        Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);
        serviceIntent.putExtra(C.EXTRA_SHOWEVENTS, showEvents);
        serviceIntent.putExtra(C.EXTRA_CATEGORY, category);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.appwidget_list, serviceIntent);
        views.setEmptyView(R.id.appwidget_list, R.id.appwidget_progressBar);

        // Set the SplashActivity intent to launch when item is clicked
        Intent appIntent = new Intent(context, SplashActivity.class);
        appIntent.putExtra(C.EXTRA_SHOWEVENTS, showEvents);
        appIntent.putExtra(C.EXTRA_CATEGORY, category);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.appwidget_list, appPendingIntent);

        String title;
        int titleBgResId;
        if (showEvents) {
            title = context.getString(R.string.events_label);
            titleBgResId = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        } else {
            title = context.getString(R.string.places_label);
            titleBgResId = ContextCompat.getColor(context, R.color.colorAccent);
        }
        views.setTextViewText(R.id.appwidget_title, title);
        views.setTextViewText(R.id.appwidget_category, category);
        views.setInt(R.id.appwidget_title_ll, "setBackgroundColor", titleBgResId);
        views.setOnClickPendingIntent(R.id.appwidget_title, appPendingIntent);
        return views;
    }

    private static RemoteViews handleEmptySource(RemoteViews views, Context context, boolean showEvents, String category, int appWidgetId) {
        views.setViewVisibility(R.id.appwidget_title_ll, View.GONE);
        views.setViewVisibility(R.id.appwidget_list_rl, View.GONE);
        views.setViewVisibility(R.id.appwidget_error_ll, View.VISIBLE);

        // Handle click on the retry button
        Intent serviceIntent = new Intent(context, WidgetService.class);
        serviceIntent.setAction(C.ACTION_UPDATE_WIDGET);
        serviceIntent.putExtra(C.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.putExtra(C.EXTRA_SHOWEVENTS, showEvents);
        serviceIntent.putExtra(C.EXTRA_CATEGORY, category);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, 123, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(context, 123, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.appwidget_retry_button, pendingIntent);

        // Handle click on the whole error layout
        Intent appIntent = new Intent(context, SplashActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_error_ll, appPendingIntent);
        return views;
    }

    private static boolean areCachedData(boolean showEvents, String category) {
        boolean areCashedData;
        if (showEvents) {
            areCashedData = category.equals(DataProvider.getInstance().getEventsCategory()) && DataProvider.getInstance().getEvents() != null;
        } else {
            areCashedData = category.equals(DataProvider.getInstance().getPlacesCategory()) && DataProvider.getInstance().getPlaces() != null;
        }
        return areCashedData;
    }
}

