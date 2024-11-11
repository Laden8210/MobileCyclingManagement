package com.example.mobilecyclingmanagement.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.NewFeedAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.gestures.OnMoveListener;


public class HomeFragment extends Fragment {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new NewFeedAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mapView = view.findViewById(R.id.mapView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeMap();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        initializeMap();
        return view;
    }

    private void initializeMap() {

        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder().zoom(14.0).build()
        );

        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS, style -> {

            getLastKnownLocation(location -> {
                if (location != null) {
                    updateMapWithLocation(location);
                } else {

                }
            });
        });
    }



    private void getLastKnownLocation(OnLocationReceivedListener listener) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            listener.onLocationReceived(null);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    listener.onLocationReceived(task.getResult());
                } else {
                    listener.onLocationReceived(null);
                }
            }
        });
    }

    private void updateMapWithLocation(Location location) {
        // Create a LatLng object from the Location
        com.mapbox.geojson.Point currentLocation = com.mapbox.geojson.Point.fromLngLat(location.getLongitude(), location.getLatitude());

        mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                .center(currentLocation)
                .zoom(15.0)
                .build()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
            } else {
                Toast.makeText(getContext(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
    }
}