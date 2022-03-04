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

package com.moworks.moranews

import android.content.Context
import androidx.room.Room
import com.moworks.moranews.data.NewsRepository
import com.moworks.moranews.data.source.local.LocalDataSource
import com.moworks.moranews.data.source.local.NewsDatabase
import com.moworks.moranews.data.source.remote.RemoteDataSource


object ServiceLocator {

    private const val dataBaseName = "news.db"

    private  var database: NewsDatabase? = null

    @Volatile
    private  var newsRepository : NewsRepository? = null

    fun provideNewsRepository(context: Context): NewsRepository {
        synchronized(this){
            return newsRepository ?: createNewsRepository(context)
        }
    }

    private fun  createNewsRepository(context: Context): NewsRepository {
        val  newRepo = NewsRepository(createNewsLocalDataSource(context) , RemoteDataSource)
        newsRepository = newRepo
        return newRepo
    }

    private fun createNewsLocalDataSource(context: Context): LocalDataSource {
        val database = database ?: createDataBase(context)
        return LocalDataSource(database.newsDao)
    }

    private fun createDataBase(context: Context): NewsDatabase {
        val result  = Room.databaseBuilder(context.applicationContext , NewsDatabase::class.java, dataBaseName)
            .fallbackToDestructiveMigration()
            .build()
        database = result
        return result
    }


}