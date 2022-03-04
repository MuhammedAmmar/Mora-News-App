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

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.R
import com.moworks.moranews.adapter.ExpandableAdapter
import com.moworks.moranews.data.source.remote.HeadlinesCategory
import com.moworks.moranews.databinding.FragmentHomeScreenBinding
import com.moworks.moranews.utils.contactDeveloper
import com.moworks.moranews.utils.resolverCompat
import com.moworks.moranews.viewModel.NewsFactory
import com.moworks.moranews.viewModel.NewsViewModel

class HomeScreenFragment : Fragment() {

    private lateinit var binding : FragmentHomeScreenBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var expandableList : ExpandableListView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeScreenBinding.inflate(inflater)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        expandableList = binding.expandableList


        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        navController = findNavController()

        NavigationUI.setupActionBarWithNavController((requireActivity() as AppCompatActivity), navController)
        NavigationUI.setupWithNavController(binding.topAppBar, navController , drawerLayout)
        NavigationUI.setupWithNavController(navigationView , navController )

        setHasOptionsMenu(true)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id != controller.graph.startDestinationId) {
                drawerLayout.close()
            }
        }

        val application = requireContext().applicationContext as NewsApplication
        val factory = NewsFactory( application , application.newsRepository)
        ViewModelProvider(this ,factory)[NewsViewModel::class.java]


        val adapter  = ViewPagerAdapter(parentFragmentManager , lifecycle )
        val mediator = TabLayoutMediator(binding.tabLayout , binding.viewPager , true){
                tab, position ->
            when (position) {
                0 -> tab.text =  resources.getStringArray(R.array.tab_titles)[0]
                1 -> tab.text = resources.getStringArray(R.array.tab_titles)[1]
            }
        }


        Handler(Looper.getMainLooper()).post {
            binding.viewPager.adapter = adapter
            mediator.attach()
        }

        setUpExpandableList()

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_screen_menu , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.contact_us ->{
                val intent = contactDeveloper(requireContext())
                resolverCompat(intent ,requireActivity(), requireView())
                true
            }
            else -> NavigationUI.onNavDestinationSelected(item , navController) || super.onOptionsItemSelected(item)
        }

    }


    @SuppressLint("ResourceType")
    private fun setUpExpandableList(){
        val headersTitles  = listOf(requireContext().getString(R.string.category_list_header))

        val topicsTitles = resources.getStringArray(R.array.list_headers).toList()

        val hashMap = HashMap<String ,List<String>>()
        hashMap[headersTitles[0]] = topicsTitles

        val categoryEnum = HeadlinesCategory.values()

        val expandableAdapter = ExpandableAdapter(requireContext() , headersTitles ,  hashMap )

        expandableList.setOnChildClickListener { _, _, _, childPosition, _ ->

            navController.navigate(HomeScreenFragmentDirections.actionMainFragmentToPagingFragment(categoryEnum[childPosition]))

            false
        }
        expandableList.setAdapter(expandableAdapter)
        expandableList.expandGroup(0)
    }
}