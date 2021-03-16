package com.example.stonks.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.stonks.data.StockDataDatabase.Companion.getDatabase

class WatchListRepository(application: Application?) {
    private val dao: StockDataDao?
    fun insertStockData(repo: StockData?) {
        StockDataDatabase.databaseWriteExecutor.execute { dao!!.insert(repo) }
    }

    val allStockData: LiveData<List<StockData?>?>?
        get() = dao!!.getAllRepos()

    init {
        val db = getDatabase(application!!)
        dao = db!!.stockDataDao()
    }
}