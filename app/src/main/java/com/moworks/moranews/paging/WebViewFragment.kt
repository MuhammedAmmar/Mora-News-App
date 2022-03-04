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
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.moworks.moranews.NewsApplication
import com.moworks.moranews.R
import com.moworks.moranews.databinding.FragmentWebViewBinding
import com.moworks.moranews.utils.resolverCompat
import com.moworks.moranews.utils.setupRefreshLayout
import com.moworks.moranews.utils.shareNewsUrl
import com.moworks.moranews.utils.showSnackbar


class WebViewFragment : Fragment() {

    private lateinit var  binding : FragmentWebViewBinding
    private lateinit var webView :WebView
    private lateinit var navController: NavController
    private lateinit var viewModel: PagingViewModel
    private lateinit var args : WebViewFragmentArgs


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWebViewBinding.inflate(inflater)

        navController = findNavController()

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolBar)
        NavigationUI.setupWithNavController(binding.toolBar, navController)

        setHasOptionsMenu(true)

        args  = WebViewFragmentArgs.fromBundle(requireArguments())

        val application = requireContext().applicationContext as NewsApplication
        val factory = PagingFactory(application , application.newsRepository)
        viewModel = ViewModelProvider(this, factory)[PagingViewModel::class.java]

        webView = binding.myWebView
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = webClient

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.included.retry.setOnClickListener {
            viewModel.refreshWebPage(webView , args.webUrl)
        }

        setupRefreshLayout(binding.swipeRefresh, webView)
        binding.swipeRefresh.setOnRefreshListener { viewModel.refreshWebPage(webView , args.webUrl) }


        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = false
            domStorageEnabled = true
            cacheMode = LOAD_CACHE_ELSE_NETWORK
            mediaPlaybackRequiresUserGesture = true
            databaseEnabled = true
        }

        webView.loadUrl(args.webUrl)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (webView.canGoBack()){
                webView.goBack()

            }else {
                isEnabled = false
                requireView().showSnackbar(getString(R.string.exit_web_view), Snackbar.LENGTH_SHORT)
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.web_view_menu , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.share_action -> {
                val intent = shareNewsUrl(requireContext() ,args.webUrl)
                resolverCompat(intent ,requireActivity() ,requireView())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        webView.stopLoading()
        viewModel.hasExitWebView()
    }


    private val webClient = object : WebViewClient() {
        private var readyToCommit :Boolean = false
        private val NOT_AVAILABLE_CONTENT_HEIGHT = 676

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            readyToCommit = true
        }


        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            binding.progressBar.visibility = View.GONE
            when(readyToCommit){
                true ->{
                    viewModel.hasBadResponse(false)
                }
                false -> {
                    if(webView.contentHeight != NOT_AVAILABLE_CONTENT_HEIGHT ) return
                    viewModel.hasBadResponse()
                }
            }
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_UNKNOWN ||
                    errorCode == ERROR_UNKNOWN || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT
                ) {
                    readyToCommit = false
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            if (error?.errorCode == ERROR_HOST_LOOKUP || error?.errorCode == ERROR_UNKNOWN ||
                error?.errorCode == ERROR_UNKNOWN || error?.errorCode == ERROR_CONNECT || error?.errorCode == ERROR_TIMEOUT){
                readyToCommit = false
            }
        }
    }

}