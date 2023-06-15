package com.example.ng

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FaveLocationRepository(private val faveLocationDao: FaveLocationDao) {

    val allFaveLocationItems: Flow<List<FaveLocationItem>> = faveLocationDao.allFaveLocationItems()

    @WorkerThread
    suspend fun insertFaveLocationItem(faveLocationItem: FaveLocationItem) {
        faveLocationDao.insertFaveLocation(faveLocationItem)
    }

    @WorkerThread
    suspend fun updateFaveLocationItem(faveLocationItem: FaveLocationItem) {
        faveLocationDao.updateFaveLocation(faveLocationItem)
    }

    @WorkerThread
    suspend fun deleteFaveLocationItem(faveLocationItem: FaveLocationItem) {
        faveLocationDao.deleteFaveLocation(faveLocationItem)
    }

}