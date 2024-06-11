package com.app.news.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.news.R
import com.app.news.adapter.BookMarksAdapter
import com.app.news.databinding.FragmentBookMarksBinding
import com.app.news.db.NewsLocalModel
import com.app.news.utils.SnackbarUtils
import com.app.news.utils.Util
import com.app.news.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookMarksFragment : Fragment() {

    private lateinit var binding: FragmentBookMarksBinding

    private val newsViewModel: NewsViewModel by viewModels()
    private lateinit var bookMarksAdapter: BookMarksAdapter

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNav)


        binding.rvBookArticles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.GONE
                } else if (dy < 0 && !bottomNavigationView.isShown) {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookMarksBinding.inflate(layoutInflater)

        val toolbar = binding.toolbar
        toolbar.title = getString(R.string.bookmark)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        initialization()
        return binding.root
    }

    private fun initialization() {

        bookMarksAdapter = BookMarksAdapter(
            onCardClicked =  this::handleOnCardButton,
            onRemoveButton = this::handleOnRemoveButton
        )

        binding.rvBookArticles.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = bookMarksAdapter
        }


        observeSavedNews()


    }

    private fun observeSavedNews() {
        viewLifecycleOwner.lifecycleScope.launch {
            newsViewModel.savedNews.collectLatest {
                bookMarksAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun handleOnCardButton(data :NewsLocalModel){
        val bundle = bundleOf("Url_key" to data.url)
        findNavController().navigate(
            R.id.action_bookMarksDestination_to_webViewFragment,
            bundle
        )
    }

    private fun handleOnRemoveButton(data: NewsLocalModel){
        viewLifecycleOwner.lifecycleScope.launch {
            newsViewModel.removeSavedNews(data)
            view?.rootView?.let {
                SnackbarUtils.showSnackbarLong(it, getString(R.string.remove_article))
            }
        }
    }


}