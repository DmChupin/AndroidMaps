package com.psu.map;

import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.psu.map.Dao.App;
import com.psu.map.Dao.AppDatabase;
import com.psu.map.Entity.MapMarker;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = App.getInstance().getDatabase();

        }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        fillMap(googleMap);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                db.markerDao().insertAll(new MapMarker(latLng.latitude, latLng.longitude));

                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                markerInfo(marker.getPosition());
                return true;
            }
        });
    }

    private void fillMap(GoogleMap googleMap){
        List<MapMarker> mapMarkers = db.markerDao().getAll();
        mMap = googleMap;

        mapMarkers.forEach(mapMarker -> {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mapMarker.latitude,mapMarker.longitude))
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        });
    }

    private void markerInfo(LatLng latLng){

        Intent intent = new Intent(this, MarkerInfoActivity.class);

        intent.putExtra("latitude", latLng.latitude);
        intent.putExtra("longitude", latLng.longitude);

        startActivity(intent);
    }
}
