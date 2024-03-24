package com.app.news.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.news.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {



    private lateinit var binding: FragmentWebViewBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.web.loadUrl(param1.toString())
        binding.web.settings.javaScriptEnabled = true
        binding.web.webViewClient = WebViewClient()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Handle the back button press here
            if (binding.web.canGoBack()) {
                binding.web.goBack()
            } else {
                // If web view cannot go back, pop the fragment from the back stack
                findNavController().navigateUp()

            }
        }
    }

    companion object{
        private const val ARG_PARAM1 = "Url_key"
    }

}