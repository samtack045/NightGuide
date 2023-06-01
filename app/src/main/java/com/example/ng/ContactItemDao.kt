package com.example.ng

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactItemDao {

    @Query("SELECT * FROM contact_item_table ORDER BY id ASC")
    fun allContactItems(): Flow<List<ContactItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactItem(contactItem: ContactItem)

    @Update
    suspend fun updateContactItem(contactItem: ContactItem)

    @Delete
    suspend fun deleteContactItem(contactItem: ContactItem)

}