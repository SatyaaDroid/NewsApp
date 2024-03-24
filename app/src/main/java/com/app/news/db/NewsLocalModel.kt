package com.app.news.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "articles")
data class NewsLocalModel(
    @field:SerializedName("publishedAt") val publishedAt: String = "",

    @field:SerializedName("author") val author: String = "",

    @field:SerializedName("urlToImage") val urlToImage: String = "",

    @field:SerializedName("description") val description: String = "",

    @field:SerializedName("source") val source: String = "",

    @field:SerializedName("title") val title: String = "",

    @PrimaryKey @field:SerializedName("url") val url: String = "",

    @field:SerializedName("content") val content: String = "",

    @field:SerializedName("created") val created: Long? = System.currentTimeMillis()

)