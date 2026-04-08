package com.example.testmapapp.domain

import android.os.Parcelable

sealed class CarGroup {
    data class Group(
        val id: String,
        val title: String
    ) : CarGroup()
}