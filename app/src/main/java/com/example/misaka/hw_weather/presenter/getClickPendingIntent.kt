package com.example.misaka.hw_weather.presenter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.IdRes
import android.widget.RemoteViews

object getClickPendingIntent{
    fun getClickPendingIntent(context: Context, @IdRes resId: Int, action: String, clazz: Class<out WeatherAppWidgetProvider>): PendingIntent {
        val intent = Intent()
        intent.setClass(context, clazz)
        intent.action = action
        intent.data = Uri.parse("id:$resId")
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun show(remoteViews: RemoteViews, context: Context, javaClazz: Class<out WeatherAppWidgetProvider>) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, javaClazz)
        manager.updateAppWidget(componentName, remoteViews)
    }
}
