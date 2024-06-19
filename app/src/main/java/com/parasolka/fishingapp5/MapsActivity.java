package com.parasolka.fishingapp5;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Настройка начальной позиции камеры
        LatLng initialPosition = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialPosition));

        // Обработчик нажатий на карту
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerAndSave(latLng);
            }
        });

        // Загрузка и отображение меток из Firebase
        loadMarkersFromFirebase();
    }

    private void addMarkerAndSave(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("New Marker");
        mMap.addMarker(markerOptions);

        saveMarkerToFirebase(latLng);
    }

    private void saveMarkerToFirebase(LatLng latLng) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> marker = new HashMap<>();
        marker.put("latitude", latLng.latitude);
        marker.put("longitude", latLng.longitude);
        marker.put("userId", userId);

        db.collection("markers").add(marker)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MapsActivity.this, "Marker saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, "Error saving marker", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMarkersFromFirebase() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("markers")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double lat = document.getDouble("latitude");
                                double lng = document.getDouble("longitude");
                                LatLng latLng = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Saved Marker"));
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Error loading markers", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
