package com.example.xoulis.xaris.athLife.ui.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.GlideApp;
import com.example.xoulis.xaris.athLife.R;
import com.example.xoulis.xaris.athLife.data.models.Event;
import com.example.xoulis.xaris.athLife.data.models.LocationPoint;
import com.example.xoulis.xaris.athLife.data.models.Place;
import com.example.xoulis.xaris.athLife.databinding.FragmentDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DetailsFragment extends Fragment {

    private static final String ARG_IS_EVENT = "is_source_event";
    private static final String ARG_SOURCE = "source_arg";
    private static final String ARG_TRANSITION_NAME = "transition_name";

    private FragmentDetailsBinding binding;

    private boolean isEvent;
    private Object source;
    private String transitionName;

    private SimpleDateFormat sdf;

    private DatabaseReference userRef;
    private DatabaseReference placeRef;

    public static DetailsFragment newInstance(boolean isEvent, Parcelable source, String transitionName) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EVENT, isEvent);
        args.putParcelable(ARG_SOURCE, source);
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        if (getArguments() != null) {
            isEvent = getArguments().getBoolean(ARG_IS_EVENT);
            source = getArguments().getParcelable(ARG_SOURCE);
            transitionName = getArguments().getString(ARG_TRANSITION_NAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorResId = isEvent ? R.color.colorPrimaryDark : R.color.colorAccent;
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), colorResId));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setHandler(this);
        binding.setIsEvent(isEvent);
        downloadStaticMap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.contentImageView.setTransitionName(transitionName);
        }

        if (isEvent) {
            binding.setEvent((Event) source);
        } else {
            binding.setPlace((Place) source);
            setDbReferences();
            fetchPlaceRating();
        }

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setDbReferences() {
        String uid = FirebaseAuth.getInstance().getUid();
        String formattedPlaceName = ((Place) source).getName().replaceAll("[.#$\\[\\]]", ",");
        userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(uid)
                .child(formattedPlaceName);

        placeRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("places")
                .child(formattedPlaceName)
                .child("stars")
                .child(uid);
    }

    private void fetchPlaceRating() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double rating = dataSnapshot.getValue(Double.class);
                    if (rating == null) {
                        rating = 0.0;
                    }
                    binding.setRating((float) rating.doubleValue());
                    binding.executePendingBindings();
                }
                setRatingChangedListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadStaticMap() {
        binding.setIsMapLoading(true);
        LocationPoint locationPoint;
        if (isEvent) {
            locationPoint = ((Event) source).getLocationPoint();
        } else {
            locationPoint = ((Place) source).getLocation();
        }
        String mapUrl = buildStaticMapUrl(locationPoint);
        GlideApp.with(getContext())
                .load(mapUrl)
                .fallback(R.drawable.error_map_image)
                .error(R.drawable.error_map_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        binding.setIsMapLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        binding.setIsMapLoading(false);
                        return false;
                    }
                })
                .into(binding.staticMapImageView);
    }

    private String buildStaticMapUrl(LocationPoint lp) {
        String placeCoordinates = lp.getLat() + "," + lp.getLng();
        return Uri.parse(C.GOOGLE_STATIC_MAPS_BASE_URL)
                .buildUpon()
                .appendQueryParameter("center", placeCoordinates)
                .appendQueryParameter("zoom", "15")
                .appendQueryParameter("size", "500x300")
                .appendQueryParameter("markers", "color:red|" + placeCoordinates)
                .appendQueryParameter("key", C.GOOGLE_STATIC_MAPS_API_KEY)
                .build()
                .toString();
    }

    private long convertDateToUnixTime(String date) {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        try {
            Date formattedDate = sdf.parse(date);
            return formattedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void onEventDateLayoutClick(Event event) {
        String unformattedDate = event.getDate();
        long dateInUnix = convertDateToUnixTime(unformattedDate);
        String eventAddress = TextUtils.isEmpty(event.getAddress()) ? getString(R.string.default_event_address) : event.getAddress();
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, eventAddress)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateInUnix);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onLocationLayoutClick(LocationPoint locationPoint, String title) {
        String qParameter = locationPoint.getLat() + "," + locationPoint.getLng() + "(" + title + ")";
        Uri geo = Uri.parse("geo:0,0")
                .buildUpon()
                .appendQueryParameter("q", qParameter)
                .appendQueryParameter("z", "15")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geo);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setRatingChangedListener() {
        binding.yourRatingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                userRef.setValue((double) rating);
                placeRef.setValue((double) rating);
            }
        });
    }
}
