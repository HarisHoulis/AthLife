package com.charis.choulis.athLife.ui.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.charis.choulis.athLife.data.DataProvider;
import com.charis.choulis.athLife.data.models.Event;
import com.charis.choulis.athLife.data.models.Place;
import com.charis.choulis.athLife.data.models.Weather;
import com.charis.choulis.athLife.C;
import com.charis.choulis.athLife.H;
import com.charis.choulis.athLife.WeatherAsyncTask;
import com.charis.choulis.athLife.ui.adapters.EventsAdapter;
import com.charis.choulis.athLife.R;
import com.charis.choulis.athLife.ui.adapters.PlacesAdapter;
import com.charis.choulis.athLife.databinding.FragmentMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment implements PlacesAdapter.PlaceClickListener,
        EventsAdapter.EventClickListener {

    private static final String ARG_SHOW_EVENTS = "show_events";
    private static final String ARG_DATA_SOURCE = "data_source";

    private FragmentMainBinding binding;

    private ArrayList dataSource;
    private boolean showEvents;

    private EventsAdapter eventsAdapter;
    private PlacesAdapter placesAdapter;

    private DatabaseReference dbReference;
    private ValueEventListener dbListener;

    public MainFragment() {
    }

    public static MainFragment newInstance(boolean showEventsArg, ArrayList dataSource) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_EVENTS, showEventsArg);
        args.putParcelableArrayList(ARG_DATA_SOURCE, dataSource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchWeatherData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            showEvents = args.getBoolean(ARG_SHOW_EVENTS);
            dataSource = args.getParcelableArrayList(ARG_DATA_SOURCE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!showEvents) {
            listenForDbChanges();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setHandler(this);
        binding.setShowEvents(showEvents);
        binding.setShowError(dataSource == null || dataSource.isEmpty());

        changeStatusBarColor();

        setDataFetchingListeners();

        setupRecyclerView();

        initSpinner();
    }

    @Override
    public void onDestroyView() {
        if (!showEvents) {
            dbReference.removeEventListener(dbListener);
        }
        super.onDestroyView();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarColorResId = showEvents ? R.color.colorPrimaryDark : R.color.colorAccent;
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), statusBarColorResId));
        }
    }

    private void setDataFetchingListeners() {
        DataProvider.getInstance().setDataFetchedListener(new DataProvider.OnDataFetchedListener() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                // Handle data only when events should be shown
                if (showEvents) {
                    binding.setShowError(events == null || events.isEmpty());
                    eventsAdapter.setEvents(events);
                    binding.setShowLoading(false);
                }
            }

            @Override
            public void onPlacesFetched(ArrayList<Place> places) {
                // Handle data only when places should be shown
                if (!showEvents) {
                    placesAdapter.setPlaces(places);
                    binding.setShowError(places == null || places.isEmpty());
                    binding.setShowLoading(false);
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setHasFixedSize(true);
        if (showEvents) {
            setupEventsAdapter();
        } else {
            setupPlacesAdapter();
        }
    }

    private void setupEventsAdapter() {
        eventsAdapter = new EventsAdapter(this);
        binding.recyclerView.setAdapter(eventsAdapter);
        eventsAdapter.setEvents(dataSource);
    }

    private void setupPlacesAdapter() {
        placesAdapter = new PlacesAdapter(this);
        binding.recyclerView.setAdapter(placesAdapter);
        placesAdapter.setPlaces(dataSource);
    }

    private void initSpinner() {
        int categoriesArraysResId = showEvents ? R.array.events_categories_array : R.array.places_categories_array;
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), categoriesArraysResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = binding.categoriesSpinner;
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = adapter.getItem(i).toString();
                fetchSpecificData(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void fetchWeatherData() {
        if (H.isNetworkAvailable(getContext())) {
            WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
            weatherAsyncTask.setWeatherDataFetchedListener(new WeatherAsyncTask.OnWeatherDataFetchedListener() {
                @Override
                public void onWeatherDataFetched(Weather weather) {
                    int weatherIconResId = getWeatherIconResId(weather);
                    binding.weatherIconView.setIconResource(getString(weatherIconResId));
                }
            });
            weatherAsyncTask.execute();
        }
    }

    private void fetchAllData() {
        DataProvider.getInstance().fetchEvents(true, C.EVENT_DEFAULT_CATEGORY);
        DataProvider.getInstance().fetchPlaces(true, C.PLACE_DEFAULT_CATEGORY);
        binding.setShowLoading(true);
    }

    private void fetchSpecificData(String category) {
        String formattedCategory = formatCategory(category);
        if (showEvents) {
            DataProvider.getInstance().fetchEvents(true, formattedCategory);
        } else {
            DataProvider.getInstance().fetchPlaces(true, formattedCategory);
        }
        fetchWeatherData();
        binding.setShowLoading(true);
    }

    private String formatCategory(String category) {
        String formattedCategory;
        if (showEvents) {
            switch (category) {
                case "Festivals":
                    formattedCategory = "festivals_parades";
                    break;
                case "Art":
                    formattedCategory = "art";
                    break;
                case "Sports":
                    formattedCategory = "sports";
                    break;
                case "Tech":
                    formattedCategory = "technology";
                    break;
                default:
                    formattedCategory = "music";
                    break;
            }
        } else {
            switch (category) {
                case "Bar":
                    formattedCategory = "bar";
                    break;
                case "Cafe":
                    formattedCategory = "cafe";
                    break;
                case "Movies":
                    formattedCategory = "movie_theater";
                    break;
                case "Club":
                    formattedCategory = "night_club";
                    break;
                default:
                    formattedCategory = "restaurant";
                    break;
            }
        }
        return formattedCategory;
    }

    private int getWeatherIconResId(Weather weather) {
        String weatherIconToSet;
        String weatherDecription = weather.getDescription();

        // Icon when it's daytime
        switch (weatherDecription) {
            case "Thunderstorm":
                weatherIconToSet = "wi_time_thunderstorm";
                break;
            case "Drizzle":
                weatherIconToSet = "wi_time_raindrops";
                break;
            case "Rain":
                weatherIconToSet = "wi_time_rain";
                break;
            case "Snow":
                weatherIconToSet = "wi_time_snow";
                break;
            case "Atmosphere":
                weatherIconToSet = "wi_time_dust";
                break;
            case "Clear":
                weatherIconToSet = "wi_time_sunny";
                break;
            case "Clouds":
                weatherIconToSet = "wi_time_cloudy";
                break;
            case "Extreme":
                weatherIconToSet = "wi_alien";
                break;
            default:
                weatherIconToSet = "wi_na";
                break;
        }

        // Determine day or night icon
        if (weather.getTimeOfDay().equals("d")) {
            weatherIconToSet = weatherIconToSet.replace("time", "day");
        } else {
            if (weatherDecription.equals("Clear")) {
                weatherIconToSet = "wi_night_clear";
            } else {
                weatherIconToSet = weatherIconToSet.replace("time", "night");
            }
        }

        // Find the actual resource ID of the icon and return it
        return getResources().getIdentifier(weatherIconToSet,
                "string", getContext().getPackageName());
    }

    public void onRetryClick(View view) {
        if (H.isNetworkAvailable(getContext())) {
            fetchAllData();
            binding.setShowError(false);
        } else {
            H.showSnackBar(view, R.string.no_internet_text);
        }
    }

    private void shareEventOrPlace(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, url));
    }

    private void showDetailsFragment(View sharedView, Parcelable source) {
        String transitionName = ViewCompat.getTransitionName(sharedView);
        DetailsFragment fragment = DetailsFragment.newInstance(showEvents, source, transitionName);
        getFragmentManager().beginTransaction()
                .addSharedElement(sharedView, transitionName)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void listenForDbChanges() {
        dbReference = FirebaseDatabase.getInstance().getReference().child("places");
        dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (placesAdapter != null && dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    placesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbReference.addValueEventListener(dbListener);
    }

    @Override
    public void onEventClick(View view, Event event) {
        showDetailsFragment(view, event);
    }

    @Override
    public void onShareEventClick(String eventUrl) {
        shareEventOrPlace(eventUrl);
    }

    @Override
    public void onPlaceClick(View view, Place place) {
        showDetailsFragment(view, place);
    }

    @Override
    public void onSharePlaceClick(String placeUrl) {
        shareEventOrPlace(placeUrl);
    }
}
