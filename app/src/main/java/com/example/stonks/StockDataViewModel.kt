package com.example.stonks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stonks.data.AnalysisResponse
import com.example.stonks.data.BalanceSheetResponse
import com.example.stonks.data.StatisticsResponse
import com.example.stonks.data.StockDataRepository

class StockDataViewModel : ViewModel() {
    private val repository: StockDataRepository = StockDataRepository()
    val statisticsData: LiveData<StatisticsResponse> = repository.getStatisticsData()
    val balanceSheetData: LiveData<BalanceSheetResponse?> = repository.getBalanceSheetData()
    val analysisData: LiveData<AnalysisResponse> = repository.getAnalysisData()

    val loadingStatus: LiveData<Int> = repository.getLoadingStatus()


    fun incrementLoadingStatus() {
        repository.incrementLoadingStatus()
    }

    @JvmName("getLoadingStatus1")
    fun getLoadingStatus(): LiveData<Int> {
        return repository.getLoadingStatus()
    }

    fun loadStockData(
        symbol: String,
        region: String
    ) {
        repository.loadStockData(symbol, region)
    }
}