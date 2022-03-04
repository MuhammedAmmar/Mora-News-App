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
import androidx.lifecycle.map
import com.moworks.moranews.data.Result
import com.moworks.moranews.domain.DataModel
import com.moworks.moranews.domain.asPopularNewsModel
import com.moworks.moranews.domain.asTopHeadLinesCategoryModel
import com.moworks.moranews.domain.asTopHeadLinesModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LocalDataSource(
    private val newsDao: NewsDao,
    private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO
): LocalSource {


    override fun getTopHeadlines():LiveData<Result<List<DataModel.TopHeadlinesModel>>>{
        return  newsDao.getTopHeadlines().map {
            when{
                it.isNotEmpty() -> Result.Success ( it.asTopHeadLinesModel())
                else  -> Result.Loading
            }
        }
    }

    override fun getPopularNews(): LiveData<Result<List<DataModel.PopularNewsModel>>> {
        return  newsDao.getPopularNews().map {
            when{
                it.isNotEmpty() -> Result.Success (it.asPopularNewsModel())
                else  -> Result.Loading
            }
        }
    }


    override fun getCategory(category: String): LiveData<Result<List<DataModel.TopHeadlinesCategoryModel>>> {
        return  newsDao.getTopHeadlinesCategories(category).map {
            when{
                it.isNotEmpty() -> Result.Success ( it.asTopHeadLinesCategoryModel())
                else  -> Result.Loading
            }
        }
    }

    override fun getAllCategories(): LiveData<Result<List<DataModel.TopHeadlinesCategoryModel>>>  {
        return  newsDao.getAllCategory().map {
            when{
                it.isNotEmpty() -> Result.Success (  it.asTopHeadLinesCategoryModel())
                else  -> Result.Loading
            }
        }
    }


    override suspend fun refreshTopHeadlines(headlines: Array<TopHeadlinesEntity>) {
        withContext(ioDispatcher){
            newsDao.insertTopHeadlines(*headlines)
        }
    }

    override suspend fun refreshCategories(categories: Array<CategoryEntity>) {
        withContext(ioDispatcher){
            newsDao.insertTopHeadlinesCategories(*categories)
        }
    }


    override suspend fun refreshPopularNews(popularNews: Array<PopularNewsEntity>) {
        withContext(ioDispatcher){
            newsDao.insertPopularNews(*popularNews)
        }
    }

    override suspend fun clearTopHeadlines() = withContext(ioDispatcher){
        newsDao.clearTopHeadlines()
    }


    override suspend fun clearCategory(category: String) = withContext(ioDispatcher){
        newsDao.clearCategory(category)
    }

    override suspend fun clearPopularNews() =    withContext(ioDispatcher){
        newsDao.clearPopularNews()
    }

}