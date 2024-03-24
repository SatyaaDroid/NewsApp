package com.app.news.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.app.news.db.NewsLocalModel
import com.app.news.db.toDbModel
import com.app.news.domain.model.ArticlesItem
import com.app.news.domain.model.SortParams
import com.app.news.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    val isDataLoading: MutableLiveData<Boolean> = MutableLiveData(false)


    private val countryFlow: MutableStateFlow<String?> = MutableStateFlow("in")
    private val categoryFlow: MutableStateFlow<String?> = MutableStateFlow("business")
    private val languageFlow: MutableStateFlow<String?> = MutableStateFlow("en")


    data class PreferenceFlows(
        val country: String?,
        val language: String?,
        val category: String?
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    val topHeadlinesFlow =
        combine(
            countryFlow,
            languageFlow,
            categoryFlow
        ) { (countryFlow, languageFlow, categoryFlow) ->
            PreferenceFlows(countryFlow, languageFlow, categoryFlow)
        }.flatMapLatest {
            repository.getSearchResultStream(null, null, it.country, it.language, it.category)
        }.cachedIn(viewModelScope)


    private var currentQuery: MutableStateFlow<String> = MutableStateFlow("latest")
    private val sortParamsFlow = MutableStateFlow(SortParams().publishedAt)

    data class QueryWithSort(
        val query: String,
        val sort: String

    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchNewsFlow = combine(
        currentQuery,
        sortParamsFlow,
    ) { (query, sort) ->
        QueryWithSort(query, sort)
    }.flatMapLatest {
        repository.getSearchResultStream(it.query, it.sort, null, null, null)
            .cachedIn(viewModelScope)
    }

    fun searchNews(query: String) {
        state["query"] = query
        currentQuery.value = state.getLiveData("query", "latest").value.toString()
    }

    fun newsSortedByUser(sort: String) {
        sortParamsFlow.value = sort
    }

    override fun onCleared() {
        state["query"] = null
        super.onCleared()
    }

    fun changeStatus(newsItem: ArticlesItem) {
        viewModelScope.launch {
            val updatedNewsItem = newsItem.copy(isSaved = true)
            repository.insertNews(toDbModel(updatedNewsItem))
        }
    }

    suspend fun removeSavedNews(newsItem: NewsLocalModel) {
        repository.deleteNews(newsItem)
    }

    val savedNews = repository.getNewsFromDataBase


}