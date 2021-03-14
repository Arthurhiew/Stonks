package com.example.stonks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stonks.data.LoadingStatus
import com.example.stonks.data.StockData
import com.example.stonks.data.StockDataRepository

class StockDataViewModel : ViewModel() {
    private val repository: StockDataRepository = StockDataRepository()

    lateinit var stockData: LiveData<StockData>
    val loadingStatus: LiveData<LoadingStatus> = repository.getLoadingStatus()

    fun getStockData(
        symbol: String,
        region: String
    ) {
        stockData = repository.getStockData(symbol, region)
    }
}