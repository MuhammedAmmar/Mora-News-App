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

package com.moworks.moranews.data.source.local

import androidx.lifecycle.LiveData
import com.moworks.moranews.data.Result
import com.moworks.moranews.domain.DataModel

interface LocalSource {

    fun getTopHeadlines(): LiveData<Result<List<DataModel.TopHeadlinesModel>>>

    fun getCategory(category :String): LiveData<Result<List<DataModel.TopHeadlinesCategoryModel>>>

    fun getAllCategories(): LiveData<Result<List<DataModel.TopHeadlinesCategoryModel>>>

    fun getPopularNews(): LiveData<Result<List<DataModel.PopularNewsModel>>>

    suspend fun refreshTopHeadlines(headlines: Array<TopHeadlinesEntity>)

    suspend fun refreshCategories(categories: Array<CategoryEntity>)

    suspend fun refreshPopularNews(popularNews: Array<PopularNewsEntity>)

    suspend fun clearTopHeadlines()

    suspend fun clearPopularNews()


    suspend fun clearCategory(category: String)
}