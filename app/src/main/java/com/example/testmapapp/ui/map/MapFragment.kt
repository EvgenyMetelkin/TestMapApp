package com.example.testmapapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.testmapapp.R
import com.example.testmapapp.domain.CarInformation
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions

class MapFragment : Fragment() {

    private val args: MapFragmentArgs by navArgs()

    private lateinit var mapView: MapView
    private var mapLibreMap: MapLibreMap? = null
    private var carMarker: Marker? = null

    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        MapLibre.getInstance(requireContext())
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val carInformation: CarInformation = args.args

        viewModel.start(carInformation)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle("https://maps.starline.ru/mapstyles/default/style.json") { _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.carPosition
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                        .collect { latLng ->
                            if (latLng != null) {
                                updateCarMarker(latLng)
                            }
                        }
                }
            }
        }
    }

    private fun updateCarMarker(latLng: LatLng) {
        val map = mapLibreMap ?: return
        if (carMarker == null) {
            carMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Car")
            )
            map.cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15.0)
                .build()
        } else {
            carMarker?.position = latLng
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