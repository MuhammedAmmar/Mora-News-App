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
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moworks.moranews.R
import com.moworks.moranews.adapter.NewsAdapter
import com.moworks.moranews.data.source.remote.HeadlinesCategory
import com.moworks.moranews.domain.DataModel


@BindingAdapter("dataModel")
fun bindRecycler(recyclerView: RecyclerView, model : List<DataModel>? ){
    val adapter = recyclerView.adapter as NewsAdapter
    if (!model.isNullOrEmpty()){
        adapter.submitList(model)
    }
}

@BindingAdapter("result")
fun bindSearchResult(recyclerView: RecyclerView, model : List<DataModel>? ){
    val adapter = recyclerView.adapter as NewsAdapter
    if (!model.isNullOrEmpty()){
        adapter.submitList(model)
    }
}


@BindingAdapter(value = ["topHeadLines","popularNews","categories"], requireAll = false)
fun bindTopStoriesRecycler(recyclerView: RecyclerView,
                           topHeadLines :List<DataModel>?,
                           popularNews : List<DataModel>?,
                           categories: List<DataModel>? ){

    val adapter = recyclerView.adapter as NewsAdapter
    val topStories = populateTopStories(recyclerView.context , topHeadLines ,popularNews , categories )
    if (!topStories.isNullOrEmpty()) {
        adapter.submitList(topStories)
    }
}
private fun populateTopStories(
    context : Context,
    headlines: List<DataModel>?,
    popularNews: List<DataModel>?,
    data :List<DataModel>? ):List<DataModel> {

    val topStories = mutableListOf<DataModel>()

    val categories = craftCategories(context , data )

    if (!headlines.isNullOrEmpty()){
        topStories.addAll(headlines)

        if (!popularNews.isNullOrEmpty())
            topStories.addAll(1,popularNews.subList((popularNews.size - popularNews.size/2 ), popularNews.size))

        if (!categories.isNullOrEmpty())
            topStories.addAll(categories)
    }
    return topStories.toList()
}


fun craftCategories(context: Context, data : List<DataModel>?): List<DataModel> {
    val categoryEnum = HeadlinesCategory.values().toList()

    val categories = mutableListOf<DataModel>()

    val dataRecycler = mutableListOf<DataModel>()

    repeat(categoryEnum.size){ index ->
        data?.map {
            when{
                (it as DataModel.TopHeadlinesCategoryModel).category.contentEquals(categoryEnum[index].query) -> dataRecycler.add(it)
            }
            it
        }
        categories.appendSection(
            dataRecycler,
            categoryEnum[index], context.resources.getStringArray(R.array.list_headers)[index]
        )
        dataRecycler.clear()
    }
    return categories.toList()
}


private fun  MutableList<DataModel>.appendSection(seection : List<DataModel>?, query : HeadlinesCategory, header :String ){
    if (!seection.isNullOrEmpty()){
        add(DataModel.CategoryHeader(header = header , categoryQuery = query))
        addAll(seection.subList(0, 2))
    }
}

