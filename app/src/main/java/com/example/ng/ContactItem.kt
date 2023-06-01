package com.example.ng

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "contact_item_table")
class ContactItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "number") var num: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

{




}