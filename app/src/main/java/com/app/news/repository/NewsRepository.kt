package com.app.news.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.news.APIService.NewsAPiInterface
import com.app.news.db.NewsDao
import com.app.news.db.NewsLocalModel
import com.app.news.db.toDbModel
import com.app.news.domain.model.ArticlesItem
import com.app.news.paging.NewsPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NewsRepository @Inject constructor(
    private val newsAPiInterface: NewsAPiInterface,
    private val newsDao: NewsDao
) {

    fun getSearchResultStream(
        query: String?,
        sortBy: String?,
        country: String?,
        language: String?,
        category: String?
    ): Flow<PagingData<ArticlesItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    newsAPiInterface,
                    query,
                    sortBy,
                    country,
                    language,
                    category
                )
            }
        ).flow

    }


    suspend fun insertNews(news: NewsLocalModel) {
        newsDao.insert(news)
    }

    suspend fun deleteNews(news: NewsLocalModel) {
        newsDao.delete(news)
    }

    val getNewsFromDataBase: Flow<List<NewsLocalModel>> = newsDao.getSavedArticles()

}