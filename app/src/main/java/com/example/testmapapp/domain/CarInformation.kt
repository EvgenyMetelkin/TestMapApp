package com.example.testmapapp.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.maplibre.android.geometry.LatLng

@Parcelize
data class CarInformation(
    val title: String,
    val lat: LatLng,
) : Parcelable, CarGroup()