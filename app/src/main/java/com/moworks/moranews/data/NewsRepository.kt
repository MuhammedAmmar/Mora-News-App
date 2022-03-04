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
import androidx.lifecycle.map
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moworks.moranews.data.source.local.LocalDataSource
import com.moworks.moranews.domain.DataModel.*
import com.moworks.moranews.utils.timeFormatBefore
import com.moworks.moranews.data.source.remote.*
import retrofit2.HttpException

class NewsRepository(private val newsLocalDataSource : LocalDataSource,
                     private val remoteDataSource: RemoteSource,
): AppRepository {

    companion object{
        const val  HEADLINES_DAYS = 10
        const val CATEGORY_DAYS = 7
    }

    override fun observeTopHeadLines(): LiveData<Result<List<TopHeadlinesModel>>> {
        return newsLocalDataSource.getTopHeadlines()
    }


    override fun observePopularNews(): LiveData<Result<List<PopularNewsModel>>> {
        return newsLocalDataSource.getPopularNews()
    }

    override fun observeCategory(category: String): LiveData<Result<List<TopHeadlinesCategoryModel>>> {
        return newsLocalDataSource.getCategory(category)
    }

    override fun observeAllCategories(): LiveData<Result<List<TopHeadlinesCategoryModel>>> {
        return newsLocalDataSource.getAllCategories()
    }


    override suspend fun loadTopHeadLines(invalidate: Boolean): Result<Any> {
        return try {
            val topHeadLines = remoteDataSource.getRemoteSource().getTopHeadLines()

            if (invalidate) newsLocalDataSource.clearTopHeadlines()

            newsLocalDataSource.refreshTopHeadlines(topHeadLines.asTopHeadLinesEntity())

            Result.Success(topHeadLines.asTopHeadLinesModel()[0])

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loadPopularNews(invalidate: Boolean): Result<Unit> {
        return try {
            val popularNews = remoteDataSource.getRemoteSource().getPopularNews(
                timeFormatBefore(
                HEADLINES_DAYS
            )
            )

            if (invalidate) newsLocalDataSource.clearPopularNews()

            newsLocalDataSource.refreshPopularNews(popularNews.asPopularNewsEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun prepareNewsCategories(invalidate: Boolean) {
        val enum = HeadlinesCategory.values()
        repeat(enum.size) {
            loadCategory(enum[it].query, interval = timeFormatBefore(CATEGORY_DAYS), invalidate = invalidate)
        }
    }


    override suspend fun loadCategory(
        category: String,
        interval: String,
        invalidate: Boolean
    ): Result<Unit> {
        return try {
            val topHeadLines = remoteDataSource.getRemoteSource()
                .getCategory(category = category, interval = interval)
            if (invalidate) newsLocalDataSource.clearCategory(category)

            newsLocalDataSource.refreshCategories(topHeadLines.asCategoryEntity(category))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun searchIn(query: String): Result<List<TopHeadlinesModel>> {
        return try {
            val response =
                remoteDataSource.getRemoteSource().getSearchInResult(query, timeFormatBefore(7))
                    .asTopHeadLinesModel()
            Result.Success(response)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun invalidateAndSyncData(): Result<Any> {
        var result: Result<Any>
        return try {
            result = loadTopHeadLines(true)
            loadPopularNews(true)
            prepareNewsCategories(true)
            result

        } catch (e: HttpException) {
            result = Result.Error(e)
            result
        }
    }

    override fun syncImageCache(context: Context) {
        observePopularNews().map {
            when (it) {
                is Result.Success -> it.data.map { model ->
                    Glide.with(context).downloadOnly().load(model.urlToImage)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).submit()
                    model
                }
                else -> return@map
            }
        }
        observeAllCategories().map {
            when(it){
                is Result.Success -> it.data.map { model ->
                    Glide.with(context).downloadOnly().load(model.urlToImage)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).submit()
                    model
                }
                else -> return@map
            }
        }
    }
}



