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

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.moworks.moranews.R
import com.moworks.moranews.domain.DataModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@BindingAdapter("urlToImage")
fun bindImage(view: ImageView, item: DataModel){
    when (item) {
        is DataModel.PopularNewsModel -> setUpGlide(view.context, view, item.urlToImage)

        is DataModel.TopHeadlinesModel -> setUpGlide(view.context, view, item.urlToImage)

        is DataModel.TopHeadlinesCategoryModel -> setUpGlide(view.context, view, item.urlToImage)

        is DataModel.OutlineCategoryModel -> setUpGlide(view.context, view, item.urlToImage)
        else -> return
    }
}

private fun setUpGlide(context: Context, view: ImageView, urlToImage: String?){
    Glide.with(context).load(urlToImage)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.error_image_holder)
        ).into(view)
}


@BindingAdapter("publishedAt")
fun bindTime(view: TextView, item: DataModel){

    when(item){
        is DataModel.PopularNewsModel -> view.text =  dateFormat(view.context ,item.publishedAt )
        is DataModel.TopHeadlinesCategoryModel -> view.text =  dateFormat(view.context , item.publishedAt)
        is DataModel.TopHeadlinesModel -> view.text =  dateFormat(view.context  , item.publishedAt , false)
        is DataModel.OutlineCategoryModel -> view.text = dateFormat(view.context , item.publishedAt , false)
        else -> return
    }

}


@BindingAdapter("sourceName")
fun bindSourceName(view: TextView, item: DataModel){
    when(item){
        is DataModel.PopularNewsModel -> allowAccessibility(view , item.sourceName )
        is DataModel.TopHeadlinesModel ->  allowAccessibility(view , item.sourceName )
        is DataModel.TopHeadlinesCategoryModel ->  allowAccessibility(view , item.sourceName )
        is DataModel.OutlineCategoryModel ->  allowAccessibility(view , item.sourceName )
        else -> return
    }
}

@BindingAdapter("content")
fun bindContent(view: TextView, item: DataModel){
    when(item){
        is DataModel.PopularNewsModel -> allowAccessibility(view , item.title )
        is DataModel.TopHeadlinesModel ->  allowAccessibility(view , item.title )
        is DataModel.TopHeadlinesCategoryModel -> allowAccessibility(view , item.title )
        is DataModel.OutlineCategoryModel -> allowAccessibility(view , item.title )
        else -> return
    }
}

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
private fun dateFormat(context: Context, publishedAt: String?, isSimpleShape : Boolean = true): String {
    return  try {
        if (!publishedAt.isNullOrEmpty()) {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val diff = System.currentTimeMillis() - parser.parse(publishedAt).time
            timeInUnit(context , diff , isSimpleShape)
        } else context.getString(R.string.just_now_form)

    }catch (e : ParseException){
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val diff = System.currentTimeMillis() - parser.parse("${publishedAt?.substring(0,19)}Z").time
        timeInUnit(context , diff , isSimpleShape)
    }
}

private fun timeInUnit(context: Context , diff :Long , isSimpleShape : Boolean ) : String {
    return if(isSimpleShape ){
        when{
            timeInDays(diff) > 0L -> context.getString(R.string.short_day_form, timeInDays(diff))
            timeInHours(diff)  in 1L..23L -> context.getString(R.string.short_hour_form, timeInHours(diff))
            timeInMinutes(diff) in 1L..59L -> context.getString(R.string.short_minute_form, timeInMinutes(diff))
            else -> context.getString(R.string.just_now_form)
        }
    }else{
        when{
            timeInDays(diff) == 1L -> context.getString(R.string.day_form, timeInDays(diff))
            timeInDays(diff) > 1L -> context.getString(R.string.many_days_form, timeInDays(diff))
            timeInHours(diff) == 1L  ->  context.getString(R.string.hour_form,  timeInHours(diff))
            timeInHours(diff)  in 2L..23L ->  context.getString(R.string.many_hours_form, timeInHours(diff))
            timeInMinutes(diff) == 1L ->  context.getString(R.string.minute_form,  timeInMinutes(diff))
            timeInMinutes(diff) in 2L..59L -> context.getString(R.string.many_minutes_form,  timeInMinutes(diff))
            else ->  context.getString(R.string.just_now_form)
        }
    }
}

private fun allowAccessibility( view: TextView ,text : String?){
    view.text = text
    view.contentDescription = text
}

private fun timeInHours(time : Long): Long {
    return TimeUnit.MILLISECONDS.toHours(time)
}
private fun timeInDays(time : Long): Long {
    return TimeUnit.MILLISECONDS.toDays(time)
}
private fun timeInMinutes(time : Long): Long {
    return TimeUnit.MILLISECONDS.toMinutes(time)
}