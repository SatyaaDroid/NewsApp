package com.app.news.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NewsLocalModel::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {


    abstract fun NewsDao(): NewsDao

    companion object {

        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabse(context).also { INSTANCE = it }
            }

        private fun buildDatabse(applicationContext: Context) =
            Room.databaseBuilder(
                applicationContext,
                NewsDatabase::class.java, "news.db"
            )
                .fallbackToDestructiveMigration()
                .build()


    }

}