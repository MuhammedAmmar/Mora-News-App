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

package com.moworks.moranews.viewModel
import android.app.Application
import androidx.lifecycle.*
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.R
import com.moworks.moranews.data.NewsRepository
import com.moworks.moranews.data.Result
import com.moworks.moranews.domain.DataModel
import kotlinx.coroutines.launch


class NewsViewModel(application: Application ,private val repository: NewsRepository) : AndroidViewModel(application){

    private val _topHeadLines = repository.observeTopHeadLines().switchMap {
        handleResult(it)
    }

    val topHeadLines :LiveData<List<DataModel>?> = _topHeadLines


    private val _popularNews = repository.observePopularNews().switchMap {
        handleResult(it)
    }

    val popularNews :LiveData<List<DataModel>?> = _popularNews


    private val _topStoriesCategorySection = repository.observeAllCategories().switchMap {
        val result = MutableLiveData<List<DataModel>>()
        when(it){
            is Result.Success -> result.value = it.data
            else ->    result.value = emptyList()
        }
        result
    }

    val topStoriesCategorySection :LiveData<List<DataModel>> = _topStoriesCategorySection


    private var _snackbarText : MutableLiveData<String> =  MutableLiveData()

    val  snackbarText : LiveData<String>
        get() = _snackbarText

    private var _dataLoading: MutableLiveData<Boolean> =  MutableLiveData()

    val  dataLoading : LiveData<Boolean>
        get() = _dataLoading

    private var _hasData: MutableLiveData<Boolean> =  MutableLiveData()

    val  hasData : LiveData<Boolean>
        get() = _hasData

    init {
        updateNewsFromRemoteSource()
    }


    fun updateNewsFromRemoteSource(refreshing : Boolean = false) {
        if (refreshing) {
            viewModelScope.launch {
                _dataLoading.value = true
                val result = repository.loadTopHeadLines()
                repository.loadPopularNews()
                handleRemoteRefreshResult(result)
            }
        } else {
            viewModelScope.launch {
                repository.loadTopHeadLines()
                repository.loadPopularNews()
                repository.prepareNewsCategories()
            }
        }
    }



    private fun handleRemoteRefreshResult(result : Result<Any>){
        when(result){
            is Result.Success -> {
                _dataLoading.value = false
            }
            else  ->  {
                _dataLoading.value = false
                showSnackbarMessage(R.string.connection_error)
            }
        }
    }


    private fun handleResult(localData : Result<List<DataModel>>): MutableLiveData<List<DataModel>?> {

        val result = MutableLiveData<List<DataModel>?>()

        _dataLoading.value = true

        if (localData is Result.Success) {
            result.value = localData.data
            _hasData.value = true
            _dataLoading.value = false
        } else {
            result.value = emptyList()
            _dataLoading.value = false
            _hasData.value = false
        }
        return result
    }


    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = getApplication<NewsApplication>().applicationContext.resources.getString(message)
    }
}
