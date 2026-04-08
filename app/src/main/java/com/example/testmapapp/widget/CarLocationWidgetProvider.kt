package com.example.testmapapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.testmapapp.R
import com.example.testmapapp.ui.map.MapViewModel
import java.io.File
import java.lang.Double
import kotlin.IntArray

class CarLocationWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidget(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == MapViewModel.ACTION_LOCATION_UPDATED) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, CarLocationWidgetProvider::class.java)
            )
            updateWidget(context, appWidgetManager, ids)
        }
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            val prefs = context.getSharedPreferences(
                MapViewModel.PREF_NAME,
                Context.MODE_PRIVATE
            )
            val latBits = prefs.getLong(MapViewModel.KEY_CAR_POSITION_LAT, Double.doubleToRawLongBits(0.0))
            val lonBits = prefs.getLong(MapViewModel.KEY_CAR_POSITION_LON, Double.doubleToRawLongBits(0.0))
            val lat = Double.longBitsToDouble(latBits)
            val lon = Double.longBitsToDouble(lonBits)
            val latStr = if (lat != 0.0) "Lat: %.6f".format(lat) else "Lat: --"
            val lonStr = if (lon != 0.0) "Lon: %.6f".format(lon) else "Lon: --"

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_car_location)
                views.setTextViewText(R.id.text_latitude, latStr)
                views.setTextViewText(R.id.text_longitude, lonStr)

                val imagePath = prefs.getString(MapViewModel.KEY_WIDGET_MAP_PATH, null)
                if (imagePath != null) {
                    val file = File(imagePath)
                    if (file.exists()) {
                        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                        views.setImageViewBitmap(R.id.widget_map_image, bitmap)
                    } else {
                        views.setImageViewResource(
                            R.id.widget_map_image,
                            android.R.color.darker_gray
                        )
                    }
                } else {
                    views.setImageViewResource(R.id.widget_map_image, android.R.color.darker_gray)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}