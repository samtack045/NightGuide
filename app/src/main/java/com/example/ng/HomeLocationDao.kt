package com.example.ng

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeLocationDao {

    @Query("SELECT * FROM home_location_table ORDER BY id ASC")
    fun allHomeLocationItems(): Flow<HomeItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeLocation(homeLocationItem: HomeItem)

    @Update
    suspend fun updateHomeLocation(homeLocationItem: HomeItem)

    @Delete
    suspend fun deleteHomeLocation(homeLocationItem: HomeItem)

}