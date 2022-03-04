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

package com.moworks.moranews.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moworks.moranews.data.source.remote.HeadlinesCategory
import com.moworks.moranews.databinding.CategoryHeaderListItemBinding
import com.moworks.moranews.databinding.NewsOutlineListItemBinding
import com.moworks.moranews.databinding.NewsSimpleListItemBinding
import com.moworks.moranews.domain.DataModel


const val  Outline_VIEW_TYPE = 111
const val  SIMPLE_VIEW_TYPE = 222
const val CATEGORY_HEADER = 333


class NewsAdapter(private val onItemClickListener: OnItemClickListener?,
                  private val onCategoryListener : OnCategoryListener?
):ListAdapter<DataModel, RecyclerView.ViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when(viewType){
            SIMPLE_VIEW_TYPE -> SimpleNewsHolder.from(parent)
            CATEGORY_HEADER -> HeadersViewHolder.from(parent)
            else -> OutlineNewsHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is SimpleNewsHolder -> holder.bind(item , onItemClickListener)
            is OutlineNewsHolder -> holder.bind(item , onItemClickListener)
            is HeadersViewHolder -> holder.bind(item , onCategoryListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when  (getItem(position)){
            is DataModel.PopularNewsModel ->  SIMPLE_VIEW_TYPE
            is DataModel.TopHeadlinesCategoryModel -> SIMPLE_VIEW_TYPE
            is DataModel.CategoryHeader  -> CATEGORY_HEADER
            else ->  Outline_VIEW_TYPE
        }
    }


    class SimpleNewsHolder private constructor(val binding : NewsSimpleListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent :ViewGroup): SimpleNewsHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = NewsSimpleListItemBinding.inflate(inflater)

                return SimpleNewsHolder(binding)
            }
        }

        fun bind(item: DataModel, onItemClickListener: OnItemClickListener?){
            binding.newsItem = item
            binding.itemClickListener = onItemClickListener
            binding.executePendingBindings()
        }
    }

    class OutlineNewsHolder private constructor(val binding : NewsOutlineListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent :ViewGroup): OutlineNewsHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = NewsOutlineListItemBinding.inflate(inflater)

                return OutlineNewsHolder(binding)
            }
        }

        fun bind(item: DataModel, onItemClickListener: OnItemClickListener?){
            binding.newsItem = item
            binding.itemClickListener = onItemClickListener
            binding.executePendingBindings()
        }
    }


    class HeadersViewHolder private constructor(val binding : CategoryHeaderListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent :ViewGroup): HeadersViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CategoryHeaderListItemBinding.inflate(inflater)

                return HeadersViewHolder(binding)
            }
        }

        fun bind(item: DataModel, onCategoryListener: OnCategoryListener?){
            binding.categoryHeader = item as DataModel.CategoryHeader
            binding.onItemListener = onCategoryListener
            binding.executePendingBindings()
        }
    }
}

class OnItemClickListener(private val operation : (String)->Unit){
    fun onClick(webUrl :String) = operation(webUrl)
}


class OnCategoryListener(private val operation : (HeadlinesCategory)->Unit){
    fun onClick( category : HeadlinesCategory) = operation(category)
}


class DiffUtils : DiffUtil.ItemCallback<DataModel>() {

    override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
        return oldItem.webUrl == newItem.webUrl
    }
}