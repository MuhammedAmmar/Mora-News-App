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

package com.moworks.moranews.domain

import androidx.room.Entity
import com.moworks.moranews.data.source.local.CategoryEntity
import com.moworks.moranews.data.source.local.PopularNewsEntity
import com.moworks.moranews.data.source.local.TopHeadlinesEntity
import com.moworks.moranews.data.source.remote.HeadlinesCategory


/**
 * App domain to  the deliver the data and manipulate the result,
 * The Model has related extension functions to map [Entity.kt] file
 * @sample [TopHeadlinesEntity] to [DataModel] .
 *
 */

sealed class DataModel {

    abstract val webUrl: String

    data class TopHeadlinesModel(
        override val webUrl: String,
        val sourceName: String?,
        val author: String?,
        val title: String?,
        val description: String?,
        val urlToImage: String?,
        val publishedAt: String?
    ): DataModel()

    data class PopularNewsModel(
        override val webUrl: String,
        val sourceName: String?,
        val author: String?,
        val title: String?,
        val description: String?,
        val urlToImage: String?,
        val publishedAt: String?
    ): DataModel()


    data class TopHeadlinesCategoryModel(
        override val webUrl: String,
        val sourceName: String?,
        val author: String?,
        val title: String?,
        val description: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val category: String
    ): DataModel()

    data class OutlineCategoryModel(
        override val webUrl: String,
        val sourceName: String?,
        val author: String?,
        val title: String?,
        val description: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val category: String
    ): DataModel()

    data class CategoryHeader(
        override val webUrl: String ="",
        val header :String,
        val categoryQuery: HeadlinesCategory
    ): DataModel()

}


fun List<TopHeadlinesEntity>.asTopHeadLinesModel() : List<DataModel.TopHeadlinesModel> {
    return  map {
        DataModel.TopHeadlinesModel(
            webUrl = it.webUrl,
            sourceName = it.sourceName,
            author = it.author,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt
        )
    }
}

fun List<PopularNewsEntity>.asPopularNewsModel(): List<DataModel.PopularNewsModel> {
    return  map {
        DataModel.PopularNewsModel(
            webUrl = it.webUrl,
            sourceName = it.sourceName,
            author = it.author,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt
        )
    }
}

fun List<CategoryEntity>.asTopHeadLinesCategoryModel(): List<DataModel.TopHeadlinesCategoryModel> {
    return map {
        DataModel.TopHeadlinesCategoryModel(
            webUrl = it.webUrl,
            sourceName = it.sourceName,
            author = it.author,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt,
            category = it.category
        )
    }
}

fun List<DataModel.TopHeadlinesCategoryModel>.asOutlineCategoryModel(): List<DataModel.OutlineCategoryModel> {
    return  map {
        DataModel.OutlineCategoryModel(
            webUrl = it.webUrl,
            sourceName = it.sourceName,
            author = it.author,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt,
            category = it.category
        )
    }
}


