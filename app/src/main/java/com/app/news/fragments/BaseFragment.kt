package com.app.news.fragments

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.news.utils.InternetConnectionObserver
import com.app.news.widget.CustomProgressDialog
import com.app.news.widget.CustomShowDialog
import dagger.hilt.android.qualifiers.ActivityContext

open class BaseFragment : Fragment() {

    private val progressDialog: Dialog by lazy {
        CustomProgressDialog(requireContext())
    }
    

    val internetConnectionObserver: InternetConnectionObserver by lazy {
        InternetConnectionObserver(requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize views and variables
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }




    /**
     * Show Loader
     */
    fun showLoader() {
        progressDialog.show()
    }

    /**
     * Hide Loader
     */
    fun hideLoader() {
        progressDialog.hide()
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}