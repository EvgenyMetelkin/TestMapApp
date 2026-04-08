package com.example.testmapapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.example.testmapapp.R
import com.example.testmapapp.ui.map.MapViewModel
import java.lang.Double
import kotlin.IntArray
import kotlin.intArrayOf

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
            val prefs: SharedPreferences = context.getSharedPreferences(
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

                val intent = Intent(context, CarLocationWidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.text_latitude, pendingIntent)
                views.setOnClickPendingIntent(R.id.text_longitude, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}