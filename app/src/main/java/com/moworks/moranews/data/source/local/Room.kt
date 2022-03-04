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
import androidx.room.*

@Dao
interface NewsDao{

    @Insert(entity = TopHeadlinesEntity::class ,onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadlines(vararg headLines: TopHeadlinesEntity)

    @Query("SELECT * FROM top_headlines ORDER BY published_at DESC ")
    fun getTopHeadlines():LiveData<List<TopHeadlinesEntity>>


    @Query("SELECT * FROM popular_categories WHERE category= :category ORDER BY published_at DESC ")
    fun getTopHeadlinesCategories(category:String ):LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM popular_categories")
    fun getAllCategory():LiveData<List<CategoryEntity>>

    @Insert(entity = CategoryEntity::class , onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadlinesCategories(vararg category: CategoryEntity)


    @Insert(entity = PopularNewsEntity::class , onConflict = OnConflictStrategy.REPLACE)
    fun insertPopularNews(vararg popularNews: PopularNewsEntity)

    @Query("SELECT * FROM popular_news ORDER BY published_at DESC " )
    fun getPopularNews():LiveData<List<PopularNewsEntity>>

    @Query("DELETE FROM popular_news " )
    fun clearPopularNews()

    @Query("DELETE FROM top_headlines " )
    fun clearTopHeadlines()

    @Query("DELETE FROM popular_categories  WHERE category =:category ")
    fun clearCategory(category : String)
}

private const val dataBaseVersion = 1

@Database(
    entities = [TopHeadlinesEntity::class , PopularNewsEntity::class, CategoryEntity::class],
    version = dataBaseVersion ,
    exportSchema = false)
abstract class NewsDatabase :RoomDatabase(){
    abstract val newsDao : NewsDao

}
