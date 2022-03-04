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

package com.moworks.moranews.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.moworks.moranews.domain.DataModel.*

interface AppRepository {

    fun observeTopHeadLines(): LiveData<Result<List<TopHeadlinesModel>>>

    fun observeCategory(category:String): LiveData<Result<List<TopHeadlinesCategoryModel>>>

    fun observeAllCategories(): LiveData<Result<List<TopHeadlinesCategoryModel>>>

    fun observePopularNews(): LiveData<Result<List<PopularNewsModel>>>

    suspend fun loadTopHeadLines(invalidate : Boolean = false): Result<Any>


    suspend fun loadPopularNews(invalidate : Boolean = false): Result<Any>

    suspend fun loadCategory(category:String, interval :String , invalidate : Boolean = false): Result<Any>

    suspend fun searchIn(query: String): Result<Any>

    suspend fun invalidateAndSyncData(): Result<Any>

    fun syncImageCache(context: Context)


    suspend fun prepareNewsCategories(invalidate: Boolean = false )
}