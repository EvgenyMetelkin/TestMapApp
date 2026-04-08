package com.example.testmapapp.data

import android.content.Context
import com.example.testmapapp.domain.CarGroup
import com.example.testmapapp.domain.CarInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.maplibre.android.geometry.LatLng

class CarRepository {
    suspend fun getCarGroups(context: Context): List<CarGroup> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("cars.json")
            .bufferedReader()
            .use { it.readText() }
        parseCarListJson(jsonString)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseCarListJson(jsonString: String): List<CarGroup> {
        val json = JSONObject(jsonString)
        val list = json.getJSONObject("cars").getJSONArray("list")
        val groupMap = mutableMapOf<String, MutableList<CarGroup>>()
        val groupOrder = mutableListOf<String>()
        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            val parent = item.getString("parent")
            if (item.has("group")) {
                val groupId = item.getString("group")
                val title = item.getString("title")
                val group = CarGroup.Group(id = groupId, title = title)
                groupMap[groupId] = mutableListOf()
                if (!groupOrder.contains(groupId)) {
                    groupOrder.add(groupId)
                }
                groupMap.getOrPut(parent) { mutableListOf() }.add(group)
            } else if (item.has("type")) {
                val title = item.getString("title")
                val type = item.getString("type")
                val lat = item.getDouble("lat")
                val lon = item.getDouble("lon")
                val car = CarInformation(
                    title = "$title ($type)",
                    lat = LatLng(lat, lon)
                )
                groupMap.getOrPut(parent) { mutableListOf() }.add(car)
            }
        }

        val result = mutableListOf<CarGroup>()
        val rootGroups = groupMap["root"] ?: emptyList()
        for (group in rootGroups) {
            if (group is CarGroup.Group) {
                result.add(group)
                val cars = groupMap[group.id] ?: emptyList()
                result.addAll(cars)
            }
        }
        return result
    }
}