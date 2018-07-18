package com.example.xoulis.xaris.athLife.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.H;
import com.example.xoulis.xaris.athLife.data.DataProvider;
import com.example.xoulis.xaris.athLife.data.models.Event;
import com.example.xoulis.xaris.athLife.R;
import com.example.xoulis.xaris.athLife.data.models.Place;
import com.example.xoulis.xaris.athLife.ui.fragments.MainFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Event> events;
    private ArrayList<Place> places;
    private boolean showEvents = true;

    public static final String EXTRA_EVENTS = "events_list_extra";
    public static final String EXTRA_PLACES = "places_list_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_main);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            events = args.getParcelableArrayList(EXTRA_EVENTS);
            places = args.getParcelableArrayList(EXTRA_PLACES);
            if (args.containsKey(C.EXTRA_SHOWEVENTS)) {
                showEvents = args.getBoolean(C.EXTRA_SHOWEVENTS);
            }
        }

        setupNavDrawer();

        if (savedInstanceState == null) {
            showMainFragment(showEvents);
        }
    }

    private void setupNavDrawer() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navView = drawerLayout.findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                if (item.isChecked()) {
                    return false;
                }
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.nav_events:
                        showMainFragment(true);
                        break;
                    case R.id.nav_places:
                        showMainFragment(false);
                        break;
                    case R.id.nav_logout:
                        logOutUser();
                        break;
                    case R.id.nav_open_source:
                        showOpenSourceLibraries();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        int categoryResId = showEvents ? R.id.nav_events : R.id.nav_places;
        navView.setCheckedItem(categoryResId);
    }

    private void showMainFragment(boolean showEvents) {
        // Get events or places, if they exist.
        // DO NOT fetch them.
        if ((events == null || events.isEmpty()) && (places == null || places.isEmpty())) {
            events = (ArrayList<Event>) DataProvider.getInstance().getEvents();
            places = (ArrayList<Place>) DataProvider.getInstance().getPlaces();
        }
        ArrayList dataSource = showEvents ? events : places;
        MainFragment fragment = MainFragment.newInstance(showEvents, dataSource);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void logOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent splashActivityIntent = new Intent(MainActivity.this, SplashActivity.class);
                        startActivity(splashActivityIntent);
                        finish();
                    }
                });
    }

    private void showOpenSourceLibraries() {
        final Notices notices = new Notices();
        // Retrofit Library
        final String name1 = "Retrofit";
        final String url1 = "https://github.com/square/retrofit";
        final String copyright1 = "Copyright 2013 Square, Inc.";
        final License license1 = new ApacheSoftwareLicense20();

        // Glide Library
        final String name2 = "Glide";
        final String url2 = "https://github.com/bumptech/glide";
        final String copyright2 = "Copyright 2014 Google, Inc.";
        final License license2 = new ApacheSoftwareLicense20();

        // WeatherIconView Library
        final String name3 = "WeatherIconView";
        final String url3 = "https://github.com/pwittchen/WeatherIconView";
        final License license3 = new ApacheSoftwareLicense20();
        final String copyright3 = "Copyright 2015 Piotr Wittchen";

        // ExpandableTextView Library
        final String name4 = "ExpandableTextView";
        final String url4 = "https://github.com/Manabu-GT/ExpandableTextView";
        final String copyright4 = "Copyright 2014 Manabu Shimobe";
        final License license4 = new ApacheSoftwareLicense20();

        // MaterialRatingBar
        final String name5 = "MaterialRatingBar";
        final String url5 = "https://github.com/DreaminginCodeZH/MaterialRatingBar";
        final String copyright5 = "Copyright 2016 Zhang Hai";
        final License license5 = new ApacheSoftwareLicense20();

        // FirebaseUI
        final String name6 = "FirebaseUI";
        final String url6 = "https://github.com/firebase/FirebaseUI-Android";
        final License license6 = new ApacheSoftwareLicense20();

        // LicensesDialog Library
        final String name7 = "LicensesDialog";
        final String url7 = "http://psdev.de";
        final String copyright7 = "Copyright 2013-2017 Philip Schiffer";
        final License license7 = new ApacheSoftwareLicense20();

        notices.addNotice(new Notice(name1, url1, copyright1, license1));
        notices.addNotice(new Notice(name2, url2, copyright2, license2));
        notices.addNotice(new Notice(name3, url3, copyright3, license3));
        notices.addNotice(new Notice(name4, url4, copyright4, license4));
        notices.addNotice(new Notice(name5, url5, copyright5, license5));
        notices.addNotice(new Notice(name6, url6, null, license6));
        notices.addNotice(new Notice(name7, url7, copyright7, license7));

        new LicensesDialog.Builder(this)
                .setNotices(notices)
                .build()
                .show();
    }
}
