package com.psu.map;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS markers (latitude REAL, longitude REAL, comment TEXT, image TEXT)");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Cursor query = db.rawQuery("SELECT * FROM markers;", null);

        mMap = googleMap;
        if(query.moveToFirst()){
            do{
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(query.getDouble(0),query.getDouble(1)))
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
            while(query.moveToNext());
        }
        query.close();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                db.execSQL("INSERT INTO markers VALUES ('" + latLng.latitude + "' , '" + latLng.longitude + "' , ' ' , ' ') ;");
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

    /*
        Add info about marker
     */
    public void markerInfo(LatLng latLng){
        Intent intent = new Intent(this, MarkerInfoActivity.class);
        intent.putExtra("latitude", Double.toString(latLng.latitude));
        intent.putExtra("longitude", Double.toString(latLng.longitude));
        startActivity(intent);
    }
}
