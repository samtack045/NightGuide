package com.example.ng

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.UUID

class FaveLocationViewModel(private val repository: FaveLocationRepository): ViewModel() {
    var faveLocationItems: LiveData<List<FaveLocationItem>> = repository.allFaveLocationItems.asLiveData()


    fun addFaveLocationItem(faveLocationItem: FaveLocationItem) = viewModelScope.launch {
        repository.insertFaveLocationItem(faveLocationItem)
    }

    fun updateFaveLocationItem(faveLocationItem: FaveLocationItem) = viewModelScope.launch {
        repository.updateFaveLocationItem(faveLocationItem)
    }

    fun deleteFaveLocationItem(faveLocationItem: FaveLocationItem) = viewModelScope.launch {
        repository.deleteFaveLocationItem(faveLocationItem)
    }

}

class FaveLocationViewModelFactory(private val repository: FaveLocationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaveLocationViewModel::class.java))
            return FaveLocationViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}