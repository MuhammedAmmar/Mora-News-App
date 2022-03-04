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

package com.moworks.moranews.paging

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.lifecycle.*
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.R
import com.moworks.moranews.data.NewsRepository
import com.moworks.moranews.data.Result
import com.moworks.moranews.domain.DataModel
import com.moworks.moranews.domain.asOutlineCategoryModel
import com.moworks.moranews.utils.timeFormatBefore
import kotlinx.coroutines.launch

class PagingViewModel ( application : Application ,private val repository: NewsRepository) : AndroidViewModel(application) {

    companion object{
        const val REFRESH_DAYS = 15
    }
    private var _categoryTitle : MutableLiveData<String> =  MutableLiveData()

    val  categoryTitle : LiveData<String>
        get() = _categoryTitle


    private var _category = _categoryTitle.switchMap { category ->
        repository.observeCategory(category).switchMap { handleToOutlineResult(it) }
    }

    val  category : LiveData<List<DataModel.OutlineCategoryModel>> = _category


    private var _searchResult : MutableLiveData<List<DataModel.TopHeadlinesModel>?> =  MutableLiveData()

    val  searchResult : LiveData<List<DataModel.TopHeadlinesModel>?>
        get() = _searchResult

    private var _dataLoading: MutableLiveData<Boolean?> =  MutableLiveData()

    val  dataLoading : LiveData<Boolean?>
        get() = _dataLoading


    private var _badResponse: MutableLiveData<Boolean?> =  MutableLiveData()

    val  badResponse : LiveData<Boolean?>
        get() = _badResponse


    private var _snackbarText : MutableLiveData<String> =  MutableLiveData()

    val  snackbarText : LiveData<String>
        get() = _snackbarText

    private var _hasData: MutableLiveData<Boolean> =  MutableLiveData()

    val  hasData : LiveData<Boolean>
        get() = _hasData


    init {
        _hasData.value = true
    }


    fun observeCategory(category: String ){
        _categoryTitle.value = category
    }


    fun refreshCategory(){
        _dataLoading.value = true
        viewModelScope.launch {
            _categoryTitle.value?.let {
                when(repository.loadCategory(it , timeFormatBefore(REFRESH_DAYS)) ){
                    is Result.Success -> _dataLoading.value = false
                    else ->  {
                        _dataLoading.value = false
                        showSnackbarMessage(R.string.connection_error)
                    }
                }
            }
        }
    }


    fun startSearchIn(query : String){
        _dataLoading.value = true
        viewModelScope.launch {

            when(val result =  repository.searchIn(query)){
                is Result.Success -> {
                    _dataLoading.value = false

                    if (result.data.isEmpty()) {
                        _badResponse.value = true
                    }else {
                        _searchResult.value = result.data
                        _badResponse.value = false
                    }
                }
                else -> {
                    _dataLoading.value = false
                    showSnackbarMessage(R.string.connection_error)
                }
            }
        }
    }



    private fun handleToOutlineResult(localData : Result<List<DataModel.TopHeadlinesCategoryModel>>)
            :LiveData<List<DataModel.OutlineCategoryModel>> {

        val result = MutableLiveData<List<DataModel.OutlineCategoryModel>>()
        _dataLoading.value = true
        if (localData is Result.Success) {
            result.value = localData.data.asOutlineCategoryModel()
            _hasData.value = true
            _dataLoading.value = false
        } else {
            result.value = emptyList()
            _dataLoading.value = false
            _hasData.value = false
            silentRefresh()
        }
        return result
    }

    private fun silentRefresh(){
        viewModelScope.launch {
            _categoryTitle.value?.let {
                when(repository.loadCategory(it , timeFormatBefore(REFRESH_DAYS)) ){
                    is Result.Success -> return@let
                    else ->  {
                        _dataLoading.value = false
                        showSnackbarMessage(R.string.connection_error)
                    }
                }
            }
        }
    }


    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value =  getApplication<NewsApplication>().applicationContext.resources.getString(message)
    }

    fun refreshWebPage(webView : WebView , url: String){
        _dataLoading.value = true
        webView.loadUrl(url)
        Handler(Looper.getMainLooper()).postDelayed({
            _dataLoading.postValue(false)
        }, 2000)
    }


    fun hasBadResponse( isBad :Boolean = true ){
        _badResponse.value = isBad
    }

    fun hasExitWebView(){
        _badResponse.value = null
        _dataLoading.value = null
    }
}