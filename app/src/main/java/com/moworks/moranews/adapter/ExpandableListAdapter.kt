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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.moworks.moranews.R
import com.moworks.moranews.databinding.ExpandableListGroupBinding
import com.moworks.moranews.databinding.ExpandableListItemBinding


class ExpandableAdapter internal constructor(
    private val context:Context,
    private val headerDataList : List<String>, // header titles
    private val childDataList : HashMap<String, List<String>> // child data in format of header title, child title
) :BaseExpandableListAdapter() {

    private var  resId  :Int?= null

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: ExpandableListGroupBinding
    private lateinit var itemBinding: ExpandableListItemBinding


    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return childDataList[headerDataList[groupPosition]]?.get(childPosition)!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return  childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {

        var convertView = view
        val holder: ItemViewHolder

        if (convertView == null) {
            itemBinding = ExpandableListItemBinding.inflate(inflater)
            convertView = itemBinding.root
            holder = ItemViewHolder()
            holder.label = itemBinding.listItem
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }
        val expandedListText = getChild(groupPosition, childPosition) as String
        holder.label!!.text = expandedListText

        return convertView
    }


    override fun getChildrenCount(groupPosition: Int): Int {
        return childDataList[headerDataList[groupPosition]]?.size!!
    }

    override fun getGroupCount(): Int {
        return headerDataList.size
    }


    override fun getGroup(groupPosition: Int): Any {
        return headerDataList[groupPosition]
    }


    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {
        var convertView = view
        val holder: GroupViewHolder

        resId = when(isExpanded){
            true ->   R.drawable.baseline_expand_less_36
            false ->  R.drawable.baseline_expand_more_36
        }


        if (convertView == null) {
            groupBinding = ExpandableListGroupBinding.inflate(inflater)

            convertView = groupBinding.root
            holder = GroupViewHolder()
            holder.label = groupBinding.listHeader
            holder.indicator = groupBinding.groupIndicator

            convertView.tag = holder

        } else {
            holder = convertView.tag as GroupViewHolder
        }

        val listTitle = getGroup(groupPosition) as String

        holder.label!!.text = listTitle
        resId?.let {
            holder.indicator!!.setImageResource(it)
        }

        return convertView
    }

    inner class ItemViewHolder {
        internal var label: TextView? = null
    }

    inner class GroupViewHolder {
        internal var label: TextView? = null
        internal var indicator :ImageView? = null
    }



    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }


    override fun hasStableIds(): Boolean {
        return false
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}