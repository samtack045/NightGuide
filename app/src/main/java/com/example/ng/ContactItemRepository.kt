package com.example.ng

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ContactItemRepository(private val contactItemDao: ContactItemDao) {

    val allContactItems: Flow<List<ContactItem>> = contactItemDao.allContactItems()

    @WorkerThread
    suspend fun insertContactItem(contactItem: ContactItem) {
        contactItemDao.insertContactItem(contactItem)
    }

    @WorkerThread
    suspend fun updateContactItem(contactItem: ContactItem) {
        contactItemDao.updateContactItem(contactItem)
    }

}