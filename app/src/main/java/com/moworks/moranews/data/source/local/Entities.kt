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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName ="top_headlines" )
data class TopHeadlinesEntity(
        @PrimaryKey
        @ColumnInfo(name = "web_url")
        val webUrl :String,
        @ColumnInfo(name = "source_name")
        val sourceName : String?,
        val author :String?,
        val title :String?,
        val description :String?,
        @ColumnInfo(name = "image_url")
        val urlToImage : String?,
        @ColumnInfo(name = "published_at")
        val publishedAt : String?
)


@Entity(tableName ="popular_news" )
data class PopularNewsEntity(
        @PrimaryKey
        @ColumnInfo(name = "web_url")
        val webUrl :String,
        @ColumnInfo(name = "source_name")
        val sourceName : String?,
        val author :String?,
        val title :String?,
        val description :String?,
        @ColumnInfo(name = "image_url")
        val urlToImage : String?,
        @ColumnInfo(name = "published_at")
        val publishedAt : String?
)



@Entity(tableName ="popular_categories" )
data class CategoryEntity(
        @PrimaryKey
        @ColumnInfo(name = "web_url")
        val webUrl :String,
        @ColumnInfo(name = "source_name")
        val sourceName : String?,
        val author :String?,
        val title :String?,
        val description :String?,
        @ColumnInfo(name = "image_url")
        val urlToImage : String?,
        @ColumnInfo(name = "published_at")
        val publishedAt : String?,
        val category:String
)




