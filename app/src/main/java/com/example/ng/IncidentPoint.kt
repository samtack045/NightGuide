package com.example.ng

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "incident_table")
data class IncidentPoint(
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name = "long") var long: Double,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

{
    constructor() : this(0.0,0.0)



}