package com.dinaldurak.mytrips.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dinaldurak.mytrips.R;
import com.dinaldurak.mytrips.model.Trip;

import java.util.ArrayList;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsViewHolder> implements Filterable {
    private ArrayList<Trip> tripsList;
    private ArrayList<Trip> tripsListFull;

    public TripsAdapter(ArrayList<Trip> tripsList){
        this.tripsList = tripsList;
        tripsListFull = new ArrayList<>(tripsList);
    }
    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trip_item, parent, false);
        TripsViewHolder holder = new TripsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripsAdapter.TripsViewHolder holder, int position) {
        String url = tripsList.get(position).getImageUrl();
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .centerCrop()
                    .into(holder.imgTrip);

        holder.tripName.setText(tripsList.get(position).getName());
        holder.tripDescription.setText(tripsList.get(position).getDescription());
        holder.tripCity.setText(tripsList.get(position).getCity());
        holder.tripDate.setText(tripsList.get(position).getVisitedDate());
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    class TripsViewHolder extends RecyclerView.ViewHolder{

        ImageView imgTrip;
        TextView tripName, tripDescription, tripCity, tripDate, tripNotes;

        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTrip = itemView.findViewById(R.id.imgTrip);
            tripName = itemView.findViewById(R.id.txtTripName);
            tripDescription = itemView.findViewById(R.id.txtTripDescription);
            tripCity = itemView.findViewById(R.id.txtTripCity);
            tripDate = itemView.findViewById(R.id.txtTripDate);
            tripNotes = itemView.findViewById(R.id.txtTripNotes);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Trip> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tripsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Trip trip : tripsListFull) {
                    if (trip.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(trip);
                    }else if (trip.getCity().toLowerCase().contains(filterPattern)){
                        filteredList.add(trip);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tripsList.clear();
            tripsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
