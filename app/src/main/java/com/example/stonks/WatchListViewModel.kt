package com.example.stonks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.stonks.data.StockData
import com.example.stonks.data.WatchListRepository

class WatchListViewModel(application: Application?) : AndroidViewModel(application!!) {
    private var repository = WatchListRepository(application)

    fun insertStockData(repo: StockData?) {
        repository.insertStockData(repo)
    }

    fun deleteStockData(repo: StockData) {
        repository.deleteStockData(repo)
    }

    val allStockData: LiveData<List<StockData?>?>?
        get() = repository.allStockData
}