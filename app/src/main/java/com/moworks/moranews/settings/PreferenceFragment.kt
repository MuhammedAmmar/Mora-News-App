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

package com.moworks.moranews.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.*
import com.moworks.moranews.R

class PreferenceFragment : PreferenceFragmentCompat() , SharedPreferences.OnSharedPreferenceChangeListener{

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_preferences)
        val sharedPref = preferenceScreen.sharedPreferences

        val generalPrefCategory = preferenceScreen.findPreference<PreferenceCategory>(getString(R.string.general_pref_category_key))!!
        textSizeResolution(generalPrefCategory)

        val dataPrefCategory = preferenceScreen.findPreference<PreferenceCategory>(getString(R.string.data_pref_category_key))!!
        syncSetup(sharedPref , dataPrefCategory)

        val notificationPrefCategory = preferenceScreen.findPreference<PreferenceCategory>(getString(R.string.notification_pref_category_key))!!
        notificationsResolution(notificationPrefCategory)
    }


    override fun onSharedPreferenceChanged(sharedPreferences : SharedPreferences?, key: String?) {
        when(val preference: Preference = key?.let { findPreference(it) }!!){
            !is CheckBoxPreference -> {
                // Updates the summary for the preference
                val value = sharedPreferences?.getString(preference.key, "")
                setPreferenceSummary(preference, value)
            }
        }
    }


    private fun setupPreferenceScreen(){

    }



    private fun syncSetup(sharedPref: SharedPreferences?, pref :PreferenceCategory?){
        if (pref == null ) return

        val count = pref.preferenceCount
        repeat(count){ index ->
            val p: Preference = pref.getPreference(index)
            if (p !is CheckBoxPreference) {
                val value = sharedPref?.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }


    private fun notificationsResolution(pref :PreferenceCategory?){
        if (pref == null ) return
        val count = pref.preferenceCount

        repeat(count){ index ->
            val p: Preference = pref.getPreference(index)
            if (p.key == getString(R.string.notification_pref_key)) {
                val intent  = Intent().apply {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O->{
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        }
                        else -> {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            addCategory(Intent.CATEGORY_DEFAULT)
                            data = Uri.parse("package:" + requireContext().packageName)
                        }
                    }
                }
                p.setOnPreferenceClickListener {
                    startActivity(intent)
                    true
                }
            }
        }

    }


    private fun textSizeResolution(pref :PreferenceCategory?){
        if (pref == null ) return
        val count = pref.preferenceCount
        repeat(count){ index ->
            val p: Preference = pref.getPreference(index)
            if (p.key == getString(R.string.text_size_pref_key)) {
                val intent  = Intent().apply {
                    action = Settings.ACTION_DISPLAY_SETTINGS
                }
                p.setOnPreferenceClickListener {
                    startActivity(intent)
                    true
                }
            }
        }
    }


    /**
     * Updates the summary for the preference
     * @param preference The preference to be updated
     * @param value      The value that the preference was updated to
     */
    private fun setPreferenceSummary(preference: Preference, value: String?) {
        if (preference is ListPreference) {
            // For list preferences, figure out the label of the selected value
            val prefIndex: Int = preference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                // Set the summary to that label
                preference.setSummary(preference.entries[prefIndex])
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}