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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.moworks.moranews.R
import com.moworks.moranews.ScrollChildSwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * Developer E-Mail  to be used at [contactDeveloper].
 *
 * {Note: it will be hidden}
 */
const val DEVELOPER_E_MAIL = "mohamed.contact88@gmail.com"

fun shareNewsUrl(context: Context , url : String): Intent {
    return ShareCompat.IntentBuilder(context)
        .setChooserTitle("share")
        .setText(url)
        .setType("text/plain")
        .createChooserIntent()
}

fun contactDeveloper(context: Context): Intent {
    return  Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_E_MAIL))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
    }
}

/**
 * handle package manager when [Intent.resolveActivity] equal [null].
 * {Note : direct interaction with [Intent.resolveActivity] no more safe just fail down silently  }
 */
fun resolverCompat(intent: Intent , activity: FragmentActivity , view: View){
    try {
        activity.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Snackbar.make(view, view.context.getString(R.string.intent_fail_message), Snackbar.LENGTH_SHORT).run {
            show()
        }
    }
}


fun timeFormatBefore(numOfDays :Int):String{
    val calendar = Calendar.getInstance().apply {
        time = Date()
        add(Calendar.DATE , -numOfDays)
    }
    return  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.red_700),
        ContextCompat.getColor(requireActivity(), R.color.red_700),
        ContextCompat.getColor(requireActivity(), R.color.red_700)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        show()
    }
}