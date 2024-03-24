package com.app.news.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.app.news.adapter.NewsAdapter
import com.app.news.databinding.FragmentSearchBinding
import com.app.news.domain.model.ArticlesItem
import com.app.news.domain.model.SortParams
import com.app.news.utils.SnackbarUtils
import com.app.news.utils.Util
import com.app.news.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : BaseFragment(), NewsAdapter.OnClickListener {

    private lateinit var binding: FragmentSearchBinding
    private val newsViewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var newsAdapter: NewsAdapter
    private lateinit var searchView: SearchView

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNav)


        binding.rvNewsSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.GONE
                } else if (dy < 0 && !bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        })


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_item, menu)
                val searchItem = menu.findItem(R.id.action_search)
                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                        return when (p0.itemId) {
                            R.id.action_search -> {

                                true
                            }

                            else -> {
                                false
                            }
                        }
                    }

                })
                searchView = searchItem.actionView as SearchView

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        searchView.apply {
                            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    newsViewModel.searchNews(query.toString())
                                    binding.rvNewsSearch.scrollToPosition(0)
                                    return true
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {
                                    return true
                                }

                            })
                        }
                        return true
                    }

                    R.id.relevance -> {
                        newsViewModel.newsSortedByUser(SortParams().relevancy)
                        binding.rvNewsSearch.scrollToPosition(0)
                        return true
                    }

                    R.id.popularity -> {
                        newsViewModel.newsSortedByUser(SortParams().popularity)
                        binding.rvNewsSearch.scrollToPosition(0)
                        return true
                    }

                    R.id.recent -> {
                        newsViewModel.newsSortedByUser(SortParams().publishedAt)
                        binding.rvNewsSearch.scrollToPosition(0)
                        return true
                    }

                    else -> false
                }

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        newsAdapter.listener = this

        newsAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                combinedLoadStates.apply {
                    rvNewsSearch.isVisible = this.source.refresh is LoadState.NotLoading
                    newsViewModel.isDataLoading.value = this.source.refresh is LoadState.Loading
                    tvRetryMsg.isVisible = this.source.refresh is LoadState.Error
                    btnRetry.isVisible = this.source.refresh is LoadState.Error
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            newsViewModel.searchNewsFlow.collectLatest { value: PagingData<ArticlesItem> ->
                newsAdapter.submitData(value)
            }
        }

        binding.rvNewsSearch.apply {
            binding.rvNewsSearch.layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }

        newsViewModel.isDataLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoader()
            } else {
                hideLoader()
            }

        }

        binding.btnRetry.setOnClickListener {
            newsAdapter.retry()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        val toolbar = binding.toolbar
        toolbar.title = getString(R.string.search) + "News"
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return binding.root
    }

    override fun onCardClick(url: String) {
        val bundle = bundleOf("Url_key" to url)
        findNavController().navigate(R.id.action_searchDestination_to_webViewFragment, bundle)
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