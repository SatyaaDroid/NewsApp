package com.app.news.db

import com.app.news.domain.model.ArticlesItem

fun toDbModel(articlesItem: ArticlesItem): NewsLocalModel =
    NewsLocalModel(
        publishedAt = articlesItem.publishedAt ?: "",
        author = articlesItem.author ?: "",
        urlToImage = articlesItem.urlToImage ?: "",
        description = articlesItem.description ?: "",
        source = articlesItem.source?.name.toString() ?: "",
        title = articlesItem.title ?: "",
        url = articlesItem.url ?: "",
        content = articlesItem.content ?: ""
    )

