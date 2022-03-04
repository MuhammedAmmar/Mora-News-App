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


import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


/**
 * The API End Points and Key
 *  @get KEY at
 *  @see[link](https://newsapi.org/docs/endpoints/everything)
 */
const val NEWS_API_KEY = "X-Api-Key:<key>"

const val everyThingEndpoint  ="v2/everything"

const val topHeadLinesEndpoint ="v2/top-headlines"

const val topHeadLinesQuery ="?sources=bbc-news,nbc-news,cnn,reuters,rte,time&language=en&pageSize=20"

const val popularQuery ="?sources=bbc-news,nbc-news,cnn,reuters,rte,time&sortBy=popularity&pageSize=35"

const val categoryQuery ="?sources=bbc-news,nbc-news,cnn,reuters,rte,time&language=en&pageSize=35"


/**
 * The API layer to be implemented,
 *  @see[link](https://newsapi.org/docs/endpoints/everything)
 */
interface NewsApi{

    @Headers(NEWS_API_KEY)
    @GET(value ="$topHeadLinesEndpoint$topHeadLinesQuery")
    suspend fun getTopHeadLines(): ArticlesContainer

    @Headers(NEWS_API_KEY)
    @GET(value ="$everyThingEndpoint$popularQuery")
    suspend fun getPopularNews(
        @Query("from") interval:String
    ): ArticlesContainer


    @Headers(NEWS_API_KEY)
    @GET(value ="$topHeadLinesEndpoint?country=us&pageSize=20")
    suspend fun getCategory(
        @Query("category") category :String,
        @Query("from") interval:String,
    ): ArticlesContainer


    @Headers(NEWS_API_KEY)
    @GET("$everyThingEndpoint$categoryQuery")
    suspend fun getSearchInResult(
        @Query("q" , encoded = true) query :String,
        @Query("from") interval:String,
        @Query("pageSize") requestSize :Int = 20
    ): ArticlesContainer
}


/**
 * enum class represents the  categories  provided by the Api,
 * @see[link](https://newsapi.org/docs/endpoints/everything)
 *
 * {NOTE : the ORDER is important  for data structure to avoid boiler plate,
 * and to represent the right category in order }.
 *
 * Note: The order must be the same as @property [R.array.list_headers]
 */

enum class HeadlinesCategory(val query:String ){
    BUSINESS("Business"),
    HEALTH("Health"),
    SPORTS("Sports"),
    SCIENCE("Science"),
    TECH("Technology"),
    ENTERTAINMENT("Entertainment")
}