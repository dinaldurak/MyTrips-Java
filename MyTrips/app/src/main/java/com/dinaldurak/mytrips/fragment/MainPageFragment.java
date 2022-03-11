package com.dinaldurak.mytrips.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dinaldurak.mytrips.R;
import com.dinaldurak.mytrips.adapter.TripsAdapter;
import com.dinaldurak.mytrips.model.Trip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageFragment extends Fragment {

    FloatingActionButton fab;
    RecyclerView rvTrip;
    TripsAdapter tripsAdapter;
    ArrayList<Trip> tripsList;

    EditText etSearchTrip;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        fab = view.findViewById(R.id.fab);
        rvTrip = view.findViewById(R.id.rv_trip);
        etSearchTrip = view.findViewById(R.id.etSearchTrip);

        //Firebase init
        databaseReference = FirebaseDatabase.getInstance().getReference("Trip");

        initRecyclerView();

        fab.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainPageFragment_to_addNewTripFragment);
        });

        etSearchTrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tripsAdapter != null) {
                    String newText = etSearchTrip.getText().toString();
                    tripsAdapter.getFilter().filter(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void initRecyclerView() {
        rvTrip.setHasFixedSize(true);
        rvTrip.setLayoutManager(new LinearLayoutManager(getContext()));

        tripsList = new ArrayList<>();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    tripsList.add(trip);
                }
                tripsAdapter = new TripsAdapter(tripsList);
                rvTrip.setAdapter(tripsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}