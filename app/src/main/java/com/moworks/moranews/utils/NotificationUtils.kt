/*
 * Copyright (C) 2021 Muhammed Ali Ammar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.moworks.moranews.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.moworks.moranews.R


const val TIME_CHANNEL_ID ="breaking_news_channel"
const val TIME_CHANNEL_NAME ="Breaking News"
const val  BREAKING_NEWS_REQUEST_CODE = 1111


fun createNotificationChannels(context : Context){
    val notificationManager = ContextCompat.getSystemService(context , NotificationManager::class.java) as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val breakingNewsChannel = NotificationChannel(TIME_CHANNEL_ID, TIME_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            .apply {
                setShowBadge(false)
                enableLights(true)
                enableVibration(true)
                lightColor = Color.RED
            }
        notificationManager.createNotificationChannels(arrayListOf(breakingNewsChannel))
    }
}


fun showBreakingNews(context : Context , contentText : String? = null, pic :Bitmap? = null){

    val contentIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.homeScreenFragment)
        .createPendingIntent()

    val picHolder = when(pic){
        null ->  BitmapFactory.decodeResource(context.resources , R.drawable.breaking_news)
        else -> pic
    }

    val style =  createBigPictureStyle (context , picHolder , contentText)

    val icon = BitmapFactory.decodeResource(context.resources, R.drawable.logo_img)

    val builder = NotificationCompat.Builder(context , TIME_CHANNEL_ID).apply{
        setAutoCancel(true)
        setContentTitle(context.getString(R.string.breaking_news_notif_title))
        setContentText(contentText)
        setContentIntent(contentIntent)
        setSmallIcon(R.drawable.logo_img)
        setLargeIcon(icon)
        priority = NotificationCompat.PRIORITY_HIGH
        setStyle(style)
        color = ContextCompat.getColor(context , R.color.red_700)
    }

    val notificationManager = ContextCompat.getSystemService(context , NotificationManager::class.java) as NotificationManager
    notificationManager.notify(BREAKING_NEWS_REQUEST_CODE , builder.build())
}

private fun createBigPictureStyle(context: Context , pic :Bitmap , contentText:String? ): NotificationCompat.BigPictureStyle {
    return NotificationCompat.BigPictureStyle()
        .bigLargeIcon(null)
        .bigPicture(pic)
        .setBigContentTitle(context.getString(R.string.breaking_news_notif_title))
        .setSummaryText(contentText)
}
