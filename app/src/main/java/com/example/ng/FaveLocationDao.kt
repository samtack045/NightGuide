package com.example.ng

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FaveLocationDao {

    @Query("SELECT * FROM fave_location_table ORDER BY id ASC")
    fun allFaveLocationItems(): Flow<List<FaveLocationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaveLocation(faveLocationItem: FaveLocationItem)

    @Update
    suspend fun updateFaveLocation(faveLocationItem: FaveLocationItem)

    @Delete
    suspend fun deleteFaveLocation(faveLocationItem: FaveLocationItem)

}