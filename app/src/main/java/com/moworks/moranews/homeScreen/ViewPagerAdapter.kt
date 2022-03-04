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

package com.moworks.moranews.homeScreen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moworks.moranews.homeScreen.sections.PopularFragment
import com.moworks.moranews.homeScreen.sections.TopStoriesFragment

private val fragmentList = listOf(
    TopStoriesFragment(),
    PopularFragment()
)

class ViewPagerAdapter( fragmentManager : FragmentManager ,  lifecycle: Lifecycle ) : FragmentStateAdapter(fragmentManager , lifecycle) {

    override fun getItemCount(): Int {
        return  fragmentList.size


    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
