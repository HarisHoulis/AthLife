package com.example.xoulis.xaris.athLife.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.data.models.Event;
import com.example.xoulis.xaris.athLife.H;
import com.example.xoulis.xaris.athLife.R;
import com.example.xoulis.xaris.athLife.data.models.Place;
import com.example.xoulis.xaris.athLife.data.DataProvider;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private ArrayList<Event> events;
    private ArrayList<Place> places;

    private boolean showEventsExtra = true;
    private String placesCategoryExtra = C.PLACE_DEFAULT_CATEGORY;
    private String eventsCategoryExtra = C.EVENT_DEFAULT_CATEGORY;

    private FirebaseAuth auth;

    private View rootView;

    private static final int RC_SIGN_IN = 11234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rootView = findViewById(R.id.splash_root);

        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(C.EXTRA_SHOWEVENTS) && args.containsKey(C.EXTRA_CATEGORY)) {
            showEventsExtra = args.getBoolean(C.EXTRA_SHOWEVENTS);
            String category = args.getString(C.EXTRA_CATEGORY);
            if (showEventsExtra) {
                eventsCategoryExtra = category;
            } else {
                placesCategoryExtra = category;
            }
        }

        DataProvider.getInstance().setDataFetchedListener(new DataProvider.OnDataFetchedListener() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                SplashActivity.this.events = events;
            }

            @Override
            public void onPlacesFetched(ArrayList<Place> places) {
                SplashActivity.this.places = places;
                startMainActivity();
            }
        });

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            fetchData();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(getAuthProviders())
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                fetchData();
            } else {
                int messageResId;
                if (response == null) {
                    // User pressed back button
                    messageResId = R.string.sign_in_canceled_title;
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    messageResId = R.string.no_internet_text;
                } else {
                    messageResId = R.string.unknown_error;
                    Log.e("Error logging user", "Sign-in error: ", response.getError());
                }
                H.showSnackBar(rootView, messageResId);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        }
    }

    private List<AuthUI.IdpConfig> getAuthProviders() {
        return Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
    }

    private void fetchData() {
        if (H.isNetworkAvailable(this)) {
            DataProvider.getInstance().fetchEvents(true, eventsCategoryExtra);
            DataProvider.getInstance().fetchPlaces(true, placesCategoryExtra);
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (events != null && !events.isEmpty() && places != null && !places.isEmpty()) {
            intent.putParcelableArrayListExtra(MainActivity.EXTRA_EVENTS, events);
            intent.putParcelableArrayListExtra(MainActivity.EXTRA_PLACES, places);
            intent.putExtra(C.EXTRA_SHOWEVENTS, showEventsExtra);
        }
        startActivity(intent);
        finish();
    }
}
