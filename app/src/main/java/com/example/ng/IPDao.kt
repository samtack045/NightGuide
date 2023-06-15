package com.example.ng

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface IPDao {

    @Query("SELECT * FROM incident_table ORDER BY id ASC")
    fun allPoints(): Flow<List<IncidentPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIP(incidentPoint: IncidentPoint)


}