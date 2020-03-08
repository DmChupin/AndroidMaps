package com.psu.map.Dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.psu.map.Entity.MapMarker;

@Database(entities = {MapMarker.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MarkerDao markerDao();
}
