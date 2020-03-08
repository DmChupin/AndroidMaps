package com.psu.map.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.psu.map.Entity.MapMarker;

import java.util.List;

@Dao
public interface MarkerDao {

    @Insert
    void insertAll(MapMarker... mapMarker);

    @Update
    void update(MapMarker mapMarker);

    @Delete
    void delete(MapMarker mapMarker);

    @Query("SELECT * FROM MapMarker")
    List<MapMarker> getAll();

    @Query("SELECT * FROM MapMarker WHERE LATITUDE LIKE :latitude AND " +
            "LONGITUDE LIKE :longitude LIMIT 1")
    MapMarker findByLOngitudeAndLatitude(Double latitude, Double longitude);
}
