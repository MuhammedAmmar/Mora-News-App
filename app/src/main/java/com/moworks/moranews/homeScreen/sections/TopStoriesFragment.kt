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

package com.moworks.moranews.homeScreen.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.adapter.NewsAdapter
import com.moworks.moranews.adapter.OnCategoryListener
import com.moworks.moranews.adapter.OnItemClickListener
import com.moworks.moranews.databinding.FragmentTopStoriesBinding
import com.moworks.moranews.homeScreen.HomeScreenFragmentDirections
import com.moworks.moranews.utils.setupRefreshLayout
import com.moworks.moranews.utils.showSnackbar
import com.moworks.moranews.viewModel.NewsFactory
import com.moworks.moranews.viewModel.NewsViewModel


class TopStoriesFragment : Fragment() {

    lateinit var  viewModel : NewsViewModel
    lateinit var  binding : FragmentTopStoriesBinding
    private lateinit var adapter : NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopStoriesBinding.inflate(inflater)

        val application = requireContext().applicationContext as NewsApplication
        val factory = NewsFactory( application , application.newsRepository)
        viewModel = ViewModelProvider(this ,factory)[NewsViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.included.retry.setOnClickListener { viewModel.updateNewsFromRemoteSource(true) }

        setupRefreshLayout(binding.swipeRefresh ,binding.recyclerView)


        adapter = NewsAdapter(
            OnItemClickListener { webUrl ->
                findNavController().navigate(HomeScreenFragmentDirections.actionMainFragmentToWebViewFragment(webUrl))},

            OnCategoryListener { category ->
                findNavController().navigate(HomeScreenFragmentDirections.actionMainFragmentToPagingFragment(category))
            }
        )


        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.snackbarText.observe(viewLifecycleOwner , Observer {
            if (!it.isNullOrEmpty()) {
                requireView().showSnackbar(it , Snackbar.LENGTH_SHORT)
            }
        })
    }
}
