package com.example.testmapapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.testmapapp.R
import com.example.testmapapp.domain.CarInformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import kotlin.random.Random

class MapFragment : Fragment() {

    private val args: MapFragmentArgs by navArgs()
    private lateinit var mapView: MapView

    private var mapLibreMap: MapLibreMap? = null
    private var carMarker: Marker? = null
    private var carPositionJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapLibre.getInstance(requireContext())
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val carInformation: CarInformation = args.args

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            mapLibreMap = map

            map.setStyle("https://maps.starline.ru/mapstyles/default/style.json") { _ ->
                val carLatLng = carInformation.lat
                map.cameraPosition = CameraPosition.Builder()
                    .target(carLatLng)
                    .zoom(15.0)
                    .build()

                carMarker = map.addMarker(
                    MarkerOptions()
                        .position(carLatLng)
                        .title("Car")
                )

                startCarLocationUpdates()
            }
        }
    }

    private fun startCarLocationUpdates() {
        carPositionJob?.cancel()
        carPositionJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(1000)
                val map = mapLibreMap ?: continue
                val car = carMarker ?: continue

                // Случайный сдвиг примерно на 100м
                val latOffset = Random.nextDouble(-0.0008, 0.0008)
                val lonOffset = Random.nextDouble(-0.0008, 0.0008)

                val currentPosition = car.position
                val newPosition = LatLng(
                    currentPosition.latitude + latOffset,
                    currentPosition.longitude + lonOffset
                )

                map.run {
                    car.position = newPosition
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        carPositionJob?.cancel()
        carPositionJob = null
        mapLibreMap?.let { map ->
            carMarker?.let { map.removeMarker(it) }
        }
        carMarker = null
        mapLibreMap = null
        mapView.onDestroy()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}