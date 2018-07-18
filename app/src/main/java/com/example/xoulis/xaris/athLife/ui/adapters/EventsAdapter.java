package com.example.xoulis.xaris.athLife.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xoulis.xaris.athLife.data.models.Event;
import com.example.xoulis.xaris.athLife.databinding.ListItemEventBinding;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    private List<Event> events;

    private final EventClickListener eventClickListener;

    public EventsAdapter(EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemEventBinding binding = ListItemEventBinding.inflate(inflater, parent, false);
        return new EventsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        }
        return events.size();
    }

    public interface EventClickListener {
        void onEventClick(View view, Event event);

        void onShareEventClick(String eventUrl);
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {
        private final ListItemEventBinding binding;

        EventsViewHolder(ListItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void bind(int listIndex) {
            Event event = events.get(listIndex);
            ViewCompat.setTransitionName(binding.eventThumbImageView, event.getTitle());
            binding.setEvent(event);
            binding.setHandler(eventClickListener);
            binding.executePendingBindings();
        }
    }
}

