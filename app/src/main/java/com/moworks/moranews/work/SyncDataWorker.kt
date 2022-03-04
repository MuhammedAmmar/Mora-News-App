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

package com.moworks.moranews.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.data.Result.Success
import com.moworks.moranews.domain.DataModel
import com.moworks.moranews.utils.showBreakingNews


class SyncDataWorker (context: Context, param : WorkerParameters)
    :CoroutineWorker(context , param){

    companion object{
        const val WORK_NAME ="SyncDataWorker"
        const val IMAGE_SYNC_KEY = "img_sync_key"
    }



    override suspend fun doWork(): Result {
        val canSyncImgs = inputData.getBoolean(IMAGE_SYNC_KEY,true)
        val repo =  (applicationContext as NewsApplication).newsRepository

        return when (val result = repo.invalidateAndSyncData()){
            is Success -> {
                prepareBreakingNews(applicationContext , result)
                if (canSyncImgs) { repo.syncImageCache(applicationContext) }
                Result.success()
            }
            else -> Result.retry()
        }
    }




    private fun prepareBreakingNews(context: Context, prefetch: Success<Any>) {
        val result = prefetch.data
        if (result is DataModel.TopHeadlinesModel ){
            val request = Glide.with(context).asBitmap().load(result.urlToImage)
                .submit(500 , 500)
            try {
                showBreakingNews(applicationContext, result.description, pic = request.get())
            }catch (e : Exception){
                showBreakingNews(applicationContext, result.description, pic = null)
            }
        }



    }
}
