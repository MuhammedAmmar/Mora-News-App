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

package com.moworks.moranews.data.source.remote

import com.moworks.moranews.data.source.local.CategoryEntity
import com.moworks.moranews.data.source.local.PopularNewsEntity
import com.moworks.moranews.data.source.local.TopHeadlinesEntity
import com.moworks.moranews.domain.DataModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 *  Class represent the top data layer to transfer from Internet that contains list of [Article]
 */

@JsonClass(generateAdapter = true)
data class ArticlesContainer(
    @Json(name = "articles")
    val articles : List<Article>?
)

/**
 * Class represent the data Sample unit for news that come over Internet
 */

@JsonClass(generateAdapter = true)
data class Article(
    @Json(name ="url")
    val webUrl :String,
    @Json(name ="source")
    val source : Source?,
    val author :String?,
    val title :String?,
    val description :String?,
    val urlToImage : String?,
    val publishedAt : String?,
)

@JsonClass(generateAdapter = true)
data class Source(
    @Json(name ="name")
    val name :String?
)


fun ArticlesContainer.asTopHeadLinesEntity(): Array<TopHeadlinesEntity> {
    return  articles!!.map {
        TopHeadlinesEntity(webUrl = it.webUrl,
            sourceName = it.source?.name,
            author = it.author ,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt)
    }.toTypedArray()
}



fun ArticlesContainer.asCategoryEntity(category :String): Array<CategoryEntity> {
    return articles!!.map {
        CategoryEntity(webUrl = it.webUrl,
            sourceName = it.source?.name,
            author = it.author ,
            title = it.title ,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt,
            category = category)
    }.toTypedArray()
}




fun ArticlesContainer.asPopularNewsEntity(): Array<PopularNewsEntity> {
    return articles!!.map {
        PopularNewsEntity(webUrl = it.webUrl,
            sourceName = it.source?.name,
            author = it.author ,
            title = it.title ,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt)
    }.toTypedArray()
}



fun ArticlesContainer.asTopHeadLinesModel(): List<DataModel.TopHeadlinesModel> {
    return articles!!.map {
        DataModel.TopHeadlinesModel(webUrl = it.webUrl,
            sourceName = it.source?.name,
            author = it.author ,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt)
    }
}





