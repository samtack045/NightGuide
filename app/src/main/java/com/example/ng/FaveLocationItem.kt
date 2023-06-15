package com.example.ng

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

@Entity(tableName = "fave_location_table")
class FaveLocationItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "longitude") var longitude : Double,
    @ColumnInfo(name = "latitude") var latitude : Double,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

{




}