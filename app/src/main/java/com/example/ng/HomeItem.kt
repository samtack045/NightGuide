package com.example.ng

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_location_table")
class HomeItem(
    @ColumnInfo(name = "location") var location : String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)


{
    constructor() : this("")



}