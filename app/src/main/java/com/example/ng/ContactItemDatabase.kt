package com.example.ng

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactItem::class, FaveLocationItem::class], version = 2, exportSchema = false)
abstract class ContactItemDatabase: RoomDatabase() {

    abstract fun contactItemDao(): ContactItemDao
    abstract fun faveLocationDao() : FaveLocationDao

    companion object {
        @Volatile
        private var INSTANCE: ContactItemDatabase? = null

        fun getDatabase(context: Context): ContactItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactItemDatabase::class.java,
                    "contact_item_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }

}