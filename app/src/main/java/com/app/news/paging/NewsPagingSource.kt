package com.app.news.paging

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.news.APIService.NewsAPiInterface
import com.app.news.domain.model.ArticlesItem
import java.io.IOException
import javax.inject.Inject

class NewsPagingSource @Inject constructor(
    private val service: NewsAPiInterface,
    private val query: String?,
    private val sort: String?,
    private val country: String?,
    private val language: String?,
    private val category: String?
) :
    PagingSource<Int, ArticlesItem>() {
    override fun getRefreshKey(state: PagingState<Int, ArticlesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        val position = params.key ?: STARTING_PAGE_INDEX
        val apiQuery = query
        return if (apiQuery != null && sort != null) {
            try {
                val response =
                    service.getSearchNewsArticles(apiQuery, sort, position, params.loadSize)
                val repos = response.articles as List<ArticlesItem>
                val nextKey = if (repos.isEmpty()) {
                    null
                } else {
                    if (
                        params.loadSize == 3 * NETWORK_PAGE_SIZE
                    ) {
                        position + 1
                    } else {
                        position + (params.loadSize / NETWORK_PAGE_SIZE)
                    }
                }
                LoadResult.Page(
                    data = repos,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
                )
            } catch (exception: IOException) {
                return LoadResult.Error(exception)
            } catch (exception: HttpException) {
                return LoadResult.Error(exception)
            }
        } else {
            try {
                val response =
                    service.getTopHeadlines(
                        position,
                        params.loadSize,
                        country.toString(),
                        language.toString(),
                        category.toString()
                    )
                val repos = response.articles as List<ArticlesItem>
                val nextKey = if (repos.isEmpty()) {
                    null
                } else {
                    position + (params.loadSize / NETWORK_PAGE_SIZE)
                }
                LoadResult.Page(
                    data = repos,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
                )
            } catch (exception: IOException) {
                return LoadResult.Error(exception)
            } catch (exception: HttpException) {
                return LoadResult.Error(exception)
            }
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 10
    }
}






