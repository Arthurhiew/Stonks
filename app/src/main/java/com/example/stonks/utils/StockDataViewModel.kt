package com.example.stonks.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stonks.data.StockData
import com.example.stonks.data.LoadingStatus
import com.example.stonks.data.StockDataRepository

class StockDataViewModel : ViewModel() {
    private val repository: StockDataRepository = StockDataRepository()
    val stockData: LiveData<StockData> = repository.getStockData()
    val loadingStatus: LiveData<LoadingStatus> = repository.getLoadingStatus()

    fun getStockData(symbol: String, region: String = "US") {
        repository.getStockData(symbol, region)
    }

}