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

package com.moworks.moranews

import android.content.SharedPreferences
import android.os.Build
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import androidx.work.*
import com.moworks.moranews.data.NewsRepository
import com.moworks.moranews.work.SyncDataWorker
import com.moworks.moranews.work.SyncDataWorker.Companion.IMAGE_SYNC_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class NewsApplication :MultiDexApplication(){

    val newsRepository : NewsRepository
        get() = ServiceLocator.provideNewsRepository(this)

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        delayedWork (sharedPreferences)
    }


    fun delayedWork( sharedPref :SharedPreferences){
        val canSyncNews = sharedPref.getBoolean(getString(R.string.background_syncing_key), true)
        val canSyncImages = sharedPref.getBoolean(getString(R.string.images_syncing_key), true)

        val syncFrequency = sharedPref.getString(getString(R.string.sync_frequency_key),
            getString(R.string.sync_every_six_hours_value))

        if (canSyncNews) {
            applicationScope.launch {
                setupRecurringWork(syncFrequency , canSyncImages)
            }
        }
    }


    private fun setupRecurringWork(syncFrequency: String?, syncImages :Boolean) {
        val timeUnit = syncFrequency?.toLong() ?: getString(R.string.sync_every_six_hours).toLong()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val inputData: Data = Data.Builder().putBoolean(IMAGE_SYNC_KEY ,syncImages).build()

        val repeatingRequest = PeriodicWorkRequestBuilder<SyncDataWorker>( timeUnit, TimeUnit.HOURS)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}