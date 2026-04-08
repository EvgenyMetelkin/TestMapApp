package com.example.testmapapp.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testmapapp.data.CarRepository
import com.example.testmapapp.domain.CarGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarListViewModel(
    application: Application,
    private val repository: CarRepository = CarRepository()
) : AndroidViewModel(application) {

    private val _items = MutableStateFlow<List<CarGroup>>(emptyList())
    val items: StateFlow<List<CarGroup>> = _items

    init {
        viewModelScope.launch {
            _items.value = repository.getCarGroups(application)
        }
    }
}

class CarListViewModelFactory(
    private val application: Application,
    private val repository: CarRepository = CarRepository()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarListViewModel::class.java)) {
            return CarListViewModel(application, repository) as T
        }
        throw IllegalArgumentException("!@#$ Unknown CarListViewModelFactory")
    }
}