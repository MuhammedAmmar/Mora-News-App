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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.R
import com.moworks.moranews.adapter.NewsAdapter
import com.moworks.moranews.adapter.OnItemClickListener
import com.moworks.moranews.databinding.FragmentSearchBinding
import com.moworks.moranews.utils.showSnackbar


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var navController :NavController
    private lateinit var  viewModel : PagingViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSearchBinding.inflate(inflater)

        navController = findNavController()
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolBar)
        NavigationUI.setupWithNavController(binding.toolBar, navController)

        val application = requireContext().applicationContext as NewsApplication
        val factory = PagingFactory(application , application.newsRepository)
        viewModel = ViewModelProvider(this, factory)[PagingViewModel::class.java]

        setHasOptionsMenu(true)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = NewsAdapter(OnItemClickListener { webUrl ->
            navController.navigate(
                SearchFragmentDirections.actionSearchFragmentToWebViewFragment(webUrl)
            )}, null)

        binding.recyclerView.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.snackbarText.observe(viewLifecycleOwner , Observer {
            if (!it.isNullOrEmpty()) {
                requireView().showSnackbar(it , Snackbar.LENGTH_LONG)
            }
        })
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_action_menu, menu)
        val searchView = menu.findItem(R.id.search_item).actionView as SearchView
        searchView.onActionViewExpanded()
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint =  getString(R.string.search_query_hint)
        searchView.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                // NO OP
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.startSearchIn(query.trim())
                    return  true
                }
                return false
            }
        })
    }

}