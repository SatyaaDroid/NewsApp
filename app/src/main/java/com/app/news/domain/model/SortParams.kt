package com.app.news.domain.model

data class SortParams(
    val relevancy: String = "relevancy",
    val popularity: String = "popularity",
    val publishedAt: String = "publishedAt"
)
