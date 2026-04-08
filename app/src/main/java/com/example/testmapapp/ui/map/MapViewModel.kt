package com.example.testmapapp.ui.map

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.testmapapp.domain.CarInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import kotlin.random.Random

class MapViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

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
    private var isSimulating = false

    private val prefs by lazy {
        application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun start(carInformation: CarInformation) {
        if (_carPosition.value == null) {
            _carPosition.value = carInformation.lat
        }
        startSimulation()
    }

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

                saveLocalAndSendBroadcast(newPosition)
            }
        }
    }

    private fun saveLocalAndSendBroadcast(newPosition: LatLng) {
        prefs.edit()
            .putLong(
                KEY_CAR_POSITION_LAT,
                java.lang.Double.doubleToRawLongBits(newPosition.latitude)
            )
            .putLong(
                KEY_CAR_POSITION_LON,
                java.lang.Double.doubleToRawLongBits(newPosition.longitude)
            )
            .apply()

        val context = getApplication<Application>()
        val intent = android.content.Intent(ACTION_LOCATION_UPDATED)
        intent.component = android.content.ComponentName(
            context,
            com.example.testmapapp.widget.CarLocationWidgetProvider::class.java
        )
        context.sendBroadcast(intent)
    }

    companion object {
        const val PREF_NAME = "car_location_storage"
        const val KEY_CAR_POSITION_LAT = "car_position_lat"
        const val KEY_CAR_POSITION_LON = "car_position_lon"
        const val ACTION_LOCATION_UPDATED = "com.example.testmapapp.ACTION_LOCATION_UPDATED"
    }
}