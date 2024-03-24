package com.app.news.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsLocalModel: NewsLocalModel)

    @Delete
    suspend fun delete(newsLocalModel: NewsLocalModel)

    @Query("SELECT * FROM ARTICLES ORDER BY created DESC")
    fun getSavedArticles(): Flow<List<NewsLocalModel>>

}