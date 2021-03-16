package com.example.stonks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stonks.data.*

class StockDataViewModel : ViewModel() {
    private val repository: StockDataRepository = StockDataRepository()
    val loadingStatisticsStatus: LiveData<LoadingStatus> = repository.getLoadingStatisticsStatus()
    val loadingBalancesheetStatus: LiveData<LoadingStatus> =
        repository.getLoadingBalanceSheetStatus()
    val loadingAnalysisStatus: LiveData<LoadingStatus> = repository.getLoadingAnalysisStatus()
    val statisticsData: LiveData<StatisticsResponse> = repository.getStatisticsData()
    val balanceSheetData: LiveData<BalanceSheetResponse?> = repository.getBalanceSheetData()
    val analysisData: LiveData<AnalysisResponse> = repository.getAnalysisData()

//    fun getStatisticsData(): LiveData<Stat isticsResponse> {
//        return repository.getStatisticsData()
//    }
//
//    fun getBalanceSheetData(): LiveData<BalanceSheetResponse> {
//        return repository.getBalanceSheetData()
//    }
//
//    fun getAnalysisData(): LiveData<AnalysisResponse> {
//        return repository.getAnalysisData()
//    }


    fun loadStockData(
        symbol: String,
        region: String
    ) {
        repository.loadStockData(symbol, region)
    }
}