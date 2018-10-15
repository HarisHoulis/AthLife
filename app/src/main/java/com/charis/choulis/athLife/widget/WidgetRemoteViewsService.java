package com.charis.choulis.athLife.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.charis.choulis.athLife.C;
import com.charis.choulis.athLife.R;
import com.charis.choulis.athLife.data.DataProvider;
import com.charis.choulis.athLife.data.models.Event;
import com.charis.choulis.athLife.data.models.Place;

import java.util.ArrayList;

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        boolean showEvents = intent.getBooleanExtra(C.EXTRA_SHOWEVENTS, true);
        String category = intent.getStringExtra(C.EXTRA_CATEGORY);
        return new ListRemoteViewsFactory(getApplicationContext(), showEvents, category);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private boolean showEvents;
        private String category;
        private ArrayList source;

        public ListRemoteViewsFactory(Context context, boolean showEvents, String category) {
            this.context = context;
            this.showEvents = showEvents;
            this.category = category;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            DataProvider.getInstance().setDataFetchedListener(new DataProvider.OnDataFetchedListener() {
                @Override
                public void onEventsFetched(ArrayList<Event> events) {
                    source = events;
                }

                @Override
                public void onPlacesFetched(ArrayList<Place> places) {
                    source = places;
                }
            });

            if (showEvents) {
                DataProvider.getInstance().fetchEvents(false, category);
            } else {
                DataProvider.getInstance().fetchPlaces(false, category);
            }
        }

        @Override
        public void onDestroy() {
            if (source != null) {
                source.clear();
            }
        }

        @Override
        public int getCount() {
            if (source != null) {
                return source.size();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            String text;
            if (showEvents) {
                text = ((Event) (source.get(i))).getTitle();
            } else {
                text = ((Place) source.get(i)).getName();
            }
            views.setTextViewText(R.id.appwidget_source_name, text);
            views.setOnClickFillInIntent(R.id.appwidget_source_name, new Intent());
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
