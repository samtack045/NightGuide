package com.example.ng

import android.app.Application

class ContactApplication: Application() {

    private val database by lazy {ContactItemDatabase.getDatabase(this)}
    val repository by lazy { ContactItemRepository(database.contactItemDao()) }
}