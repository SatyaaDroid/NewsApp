package com.app.news.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.news.R
import com.app.news.adapter.TopNewsAdapter
import com.app.news.databinding.FragmentHomeBinding
import com.app.news.domain.model.ArticlesItem
import com.app.news.utils.SnackbarUtils
import com.app.news.utils.Util
import com.app.news.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TopHeadlineFragment : BaseFragment(), TopNewsAdapter.OnClickListener {

    private lateinit var _binding: FragmentHomeBinding

    private val newsViewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var newsAdapter: TopNewsAdapter

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bottomNavigationView = requireActivity().findViewById(R.id.bottomNav)


        _binding.rvArticles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.GONE
                } else if (dy < 0 && !bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        })


        newsAdapter.listener = this

        newsAdapter.addLoadStateListener { combinedLoadStates ->
            _binding.apply {
                combinedLoadStates.apply {
                    rvArticles.isVisible = this.source.refresh is LoadState.NotLoading
                    newsViewModel.isDataLoading.value = this.source.refresh is LoadState.Loading
                    btnRetry.isVisible = this.source.refresh is LoadState.Error
                    tvRetryMsg.isVisible = this.source.refresh is LoadState.Error

                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                try {
                    Log.e("news", "start")
                    newsViewModel.topHeadlinesFlow.collectLatest { value: PagingData<ArticlesItem> ->
                        value.let {
                            newsAdapter.submitData(it)
                        }

                    }
                } catch (e: Exception) {
                    Log.e("news", "exception ${e.message}")
                } finally {
                    Log.e("news", "finnaly")

                }

            }
        }


        _binding.rvArticles.apply {
            _binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }

        newsViewModel.isDataLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoader()
            } else {
                hideLoader()
            }

        }

        _binding.btnRetry.setOnClickListener {
            newsAdapter.retry()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment


        val toolbar = _binding.toolbar
        toolbar.title = getString(R.string.headlines)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return _binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    // Implement the interface methods
    override fun onCardClick(url: String) {
        // Handle card click event
        val bundle = bundleOf("Url_key" to url) // Rep
        findNavController().navigate(R.id.action_homeDestination_to_webViewFragment, bundle)
    }

    override fun onSaveButton(newsItem: ArticlesItem) {
        newsViewModel.changeStatus(newsItem)
        view?.rootView?.let {
            SnackbarUtils.showSnackbarShort(it, getString(R.string.save_article))
        }
    }

    override fun onShareButton(newsItem: ArticlesItem) {
        Util.shareNews(requireContext(), newsItem.title, newsItem.urlToImage)

    }


}