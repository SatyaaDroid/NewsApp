package com.app.news.APIService

import com.app.news.BuildConfig
import com.app.news.domain.model.NewsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsAPiInterface {

    companion object {
        const val BASE_URL = BuildConfig.BASE_API
        const val KEY = BuildConfig.API_KEY
    }


    @Headers("X-Api-Key: $KEY ")
    @GET("v2/everything")
    suspend fun getSearchNewsArticles(
        @Query("q") query: String,
        @Query("sortBy") sortParams: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): NewsSearchResponse

    @Headers("X-Api-Key: $KEY ")
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("country") country: String?,
        @Query("language") language: String?,
        @Query("category") category: String?,
    ): NewsSearchResponse
}