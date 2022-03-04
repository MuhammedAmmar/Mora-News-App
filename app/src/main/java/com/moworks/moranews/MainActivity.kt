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
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.moworks.moranews.databinding.ActivityMainBinding
import com.moworks.moranews.utils.createNotificationChannels
import com.moworks.moranews.work.SyncDataWorker


class MainActivity : AppCompatActivity() , SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var navController :NavController
    private lateinit var sharedPreferences  :SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Clone)
        DataBindingUtil.setContentView<ActivityMainBinding>(this@MainActivity ,  R.layout.activity_main)
        navController = findNavController(R.id.navHost)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        createNotificationChannels(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
        if( key == getString(R.string.background_syncing_key)) {
            when(sharedPref?.getBoolean(key,true)){
                true -> (this.applicationContext as NewsApplication).delayedWork(sharedPref)
                false-> WorkManager.getInstance(this).cancelUniqueWork(SyncDataWorker.WORK_NAME)
                else -> return
            }
        }else if ( key == getString(R.string.sync_frequency_key)){
            WorkManager.getInstance(this).cancelUniqueWork(SyncDataWorker.WORK_NAME)
            sharedPref?.let { (this.applicationContext as NewsApplication).delayedWork(it) }
        }
    }

}