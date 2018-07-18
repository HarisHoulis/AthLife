package com.example.xoulis.xaris.athLife.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xoulis.xaris.athLife.data.models.Place;
import com.example.xoulis.xaris.athLife.databinding.ListItemPlaceBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    private List<Place> places;

    private final PlaceClickListener listener;

    public PlacesAdapter(PlaceClickListener placeClickListener) {
        this.listener = placeClickListener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemPlaceBinding binding = ListItemPlaceBinding.inflate(inflater, parent, false);
        return new PlacesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        }
        return 0;
    }

    public interface PlaceClickListener {
        void onPlaceClick(View view, Place place);

        void onSharePlaceClick(String placeUrl);
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        private final ListItemPlaceBinding binding;

        PlacesViewHolder(ListItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int listIndex) {
            Place place = places.get(listIndex);
            ViewCompat.setTransitionName(binding.placeThumbnail, place.getName());
            binding.setPlace(place);
            binding.setHandler(listener);
            binding.executePendingBindings();

            // Configure rating bar
            String formattedStoreName = place.getName().replaceAll("[.#$\\[\\]]", ",");
            configureRatingBar(formattedStoreName);
        }

        private void configureRatingBar(final String name) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("places")
                    .child(name)
                    .child("stars");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        double total = 0;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Double value = child.getValue(Double.class);
                            if (value == null) {
                                continue;
                            }
                            total += value;
                        }
                        double overallRating = total / dataSnapshot.getChildrenCount();
                        binding.placeOverallRating.setRating((float) overallRating);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
