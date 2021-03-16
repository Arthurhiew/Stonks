package com.example.stonks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(entities = [StockData::class], version = 1)
abstract class StockDataDatabase : RoomDatabase() {
    abstract fun stockDataDao(): StockDataDao?

    companion object {
        @Volatile
        private var INSTANCE: StockDataDatabase? = null
        private const val NUM_THREADS = 4
        @JvmField
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUM_THREADS)
        @JvmStatic
        fun getDatabase(context: Context): StockDataDatabase? {
            if (INSTANCE == null) {
                synchronized(StockDataDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            StockDataDatabase::class.java,
                            "stock_data.db"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}