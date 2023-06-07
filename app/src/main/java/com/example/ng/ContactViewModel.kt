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

class ContactViewModel(private val repository: ContactItemRepository): ViewModel() {
    var contactItems: LiveData<List<ContactItem>> = repository.allContactItems.asLiveData()


    fun addContactItem(newContact: ContactItem) = viewModelScope.launch {
        repository.insertContactItem(newContact)
    }

    fun updateContactItem(contactItem: ContactItem) = viewModelScope.launch {
        repository.updateContactItem(contactItem)
    }

    fun deleteContactItem(contactItem: ContactItem) = viewModelScope.launch {
        repository.deleteContactItem(contactItem)
    }

}

class ContactItemModelFactory(private val repository: ContactItemRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java))
            return ContactViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}