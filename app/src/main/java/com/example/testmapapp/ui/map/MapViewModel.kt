package com.example.testmapapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.testmapapp.domain.CarInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.maplibre.android.geometry.LatLng
import kotlin.random.Random

class MapViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    private val _carPosition = MutableStateFlow(
        state.run {
            val lat = get<Double>(KEY_CAR_POSITION_LAT)
            val lon = get<Double>(KEY_CAR_POSITION_LON)
            if (lat != null && lon != null) {
                LatLng(lat, lon)
            } else {
                null
            }
        }
    )
    val carPosition: StateFlow<LatLng?> = _carPosition

    fun start(carInformation: CarInformation) {
        if (_carPosition.value == null) {
            _carPosition.value = carInformation.lat
        }
        startSimulation()
    }

    private var isSimulating = false

    private fun startSimulation() {
        if (isSimulating) return
        isSimulating = true
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val cur = _carPosition.value ?: continue
                val latOffset = Random.nextDouble(-0.0008, 0.0008)
                val lonOffset = Random.nextDouble(-0.0008, 0.0008)
                val newPosition = LatLng(
                    cur.latitude + latOffset,
                    cur.longitude + lonOffset
                )
                _carPosition.value = newPosition
                state[KEY_CAR_POSITION_LAT] = newPosition.latitude
                state[KEY_CAR_POSITION_LON] = newPosition.longitude
            }
        }
    }

    companion object {
        private const val KEY_CAR_POSITION_LAT = "car_position_lat"
        private const val KEY_CAR_POSITION_LON = "car_position_lon"
    }
}