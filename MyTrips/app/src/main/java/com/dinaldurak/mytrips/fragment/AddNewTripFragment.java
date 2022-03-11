package com.dinaldurak.mytrips.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dinaldurak.mytrips.R;
import com.dinaldurak.mytrips.model.Trip;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class AddNewTripFragment extends Fragment implements View.OnClickListener {

    TextInputEditText etTripName, etTripCity, etTripDescription, etTripDate;
    Button btnAddTrip;
    ImageView imgTrip;

    Trip trip;
    DatabaseReference dbReference;
    StorageReference mStorageReference;
    Uri imgUri;

    ProgressDialog progressDialog;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_new_trip, container, false);

        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        dbReference = FirebaseDatabase.getInstance().getReference().child("Trip");

        trip = new Trip();

        etTripName = view.findViewById(R.id.etTripName);
        etTripCity = view.findViewById(R.id.etCity);
        etTripDescription = view.findViewById(R.id.etTripDescription);
        etTripDate = view.findViewById(R.id.etTripDate);
        btnAddTrip = view.findViewById(R.id.btnAdd);
        imgTrip = view.findViewById(R.id.imgTrip);

        progressDialog = new ProgressDialog(getContext());

        btnAddTrip.setOnClickListener(this);
        imgTrip.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                addTrip();
                return;
            case R.id.imgTrip:
                openFileChooser();
                return;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            imgTrip.setImageURI(imgUri);
        }
    }

    private void addTrip() {
        String name = etTripName.getText().toString().trim();
        String city = etTripCity.getText().toString().trim();
        String description = etTripDescription.getText().toString().trim();
        String date = etTripDate.getText().toString().trim();

        if (name.isEmpty()) {
            etTripName.setError("Gezi Adı girilmesi gereklidir.");
            etTripName.requestFocus();
            return;
        }
        if (city.isEmpty()) {
            etTripCity.setError("Şehir girilmesi gereklidir.");
            etTripCity.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etTripDescription.setError("Gezi Açıklaması girilmesi gereklidir.");
            etTripDescription.requestFocus();
            return;
        }
        if (date.isEmpty()) {
            etTripDate.setError("Gezi Tarihi girilmesi gereklidir.");
            etTripDate.requestFocus();
            return;
        }

        progressDialog.show();

        StorageReference storageReference = mStorageReference.child(imgUri.getLastPathSegment());
        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> dowloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        progressDialog.hide();
                        String url = task.getResult().toString();

                        trip.setName(name);
                        trip.setCity(city);
                        trip.setDescription(description);
                        trip.setVisitedDate(date);
                        trip.setImageUrl(url);

                        dbReference.push().setValue(trip);
                    }
                });
                Toast.makeText(getContext(), "Gezi başarılı bir şekilde kaydedildi!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_addNewTripFragment_to_mainPageFragment);
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}