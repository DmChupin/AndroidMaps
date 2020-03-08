package com.psu.map.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MapMarker {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "LATITUDE")
    public Double latitude;

    @ColumnInfo(name = "LONGITUDE")
    public Double longitude;

    @ColumnInfo(name = "COMMENT")
    public String comment;

    @ColumnInfo(name = "FILE_PATH")
    public String filePath;

    public MapMarker(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
