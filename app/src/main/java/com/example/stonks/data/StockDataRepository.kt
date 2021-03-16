package com.example.stonks.data

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stonks.utils.YahooApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StockDataRepository {
    private val statisticsData = MutableLiveData<StatisticsResponse>()
    private val balanceSheetData = MutableLiveData<BalanceSheetResponse>()
    private val analysisData = MutableLiveData<AnalysisResponse>()
    private val loadingStatisticsStatus = MutableLiveData<LoadingStatus>()
    private val loadingBalanceSheetStatus = MutableLiveData<LoadingStatus>()
    private val loadingAnalysisStatus = MutableLiveData<LoadingStatus>()
    private val loadingStatus = MutableLiveData<LoadingStatus>()
    private var currentSymbol: String? = null
    private val yahooApiService: YahooApiService
    private val TAG = StockDataRepository::class.java.simpleName
    private val BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"

    init {
        loadingStatisticsStatus.value = LoadingStatus.SUCCESS
        loadingBalanceSheetStatus.value = LoadingStatus.SUCCESS
        loadingAnalysisStatus.value = LoadingStatus.SUCCESS

        // build retrofit object
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        yahooApiService = retrofit.create(YahooApiService::class.java)
    }

    fun getLoadingStatus(): LiveData<LoadingStatus> {
        return loadingStatus
    }

    fun getStatisticsData(): LiveData<StatisticsResponse> {
        return statisticsData
    }

    fun getBalanceSheetData(): LiveData<BalanceSheetResponse> {
        return balanceSheetData
    }

    fun getAnalysisData(): LiveData<AnalysisResponse> {
        return analysisData
    }

    fun loadStockData(symbol: String, region: String) {
        fetchAnalysisData(symbol, region)
        fetchBalanceSheetData(symbol, region)
        fetchStatisticsData(symbol, region)
    }

//        var shortLongTermDebt =
//            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortLongTermDebt?.value
//        var longTermDebt =
//            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.longTermDebt?.value
//        var cash =
//            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.cash?.value
//        var shortTermInvestments =
//            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortTermInvestments?.value


//        var growthRate = analysisData.value?.earningsTrend?.trend?.get(4)?.growth?.value
//
    ///// Statistics Data
//        stockData.value!!.symbol = symbol
//        stockData.value!!.curOpCashFlow = curOpCashFlow
//        stockData.value!!.lastClose = lastClose
//        stockData.value!!.sharesOutstanding = sharesOutstanding
//
//        if (shortLongTermDebt == null) {
//            shortLongTermDebt = 0L
//        }
//
//        if (longTermDebt == null) {
//            longTermDebt = 0L
//        }
//
//        if (cash == null) {
//            cash = 0L
//        }
//
//        if (shortTermInvestments == null) {
//            shortTermInvestments = 0L
//        }
//
//        if (growthRate == null) {
//            growthRate = 0F
//        }
//
//        stockData.value!!.totalDebt = shortLongTermDebt + longTermDebt
//        stockData.value!!.cashNShortTermInvestment = cash + shortTermInvestments
//        stockData.value!!.growthRate = growthRate
//        return stockData
//    }

    fun getLoadingStatisticsStatus(): LiveData<LoadingStatus> {
        return loadingStatisticsStatus
    }

    fun getLoadingBalanceSheetStatus(): LiveData<LoadingStatus> {
        return loadingBalanceSheetStatus
    }

    fun getLoadingAnalysisStatus(): LiveData<LoadingStatus> {
        return loadingAnalysisStatus
    }


    // first fetch
    private fun fetchStatisticsData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
//        if (shouldExecuteFetch(symbol)) {
        currentSymbol = symbol
        loadingStatisticsStatus.value = LoadingStatus.LOADING
        val statisticsResult = yahooApiService.getStatistics(symbol, region)
        statisticsResult.enqueue(object : Callback<StatisticsResponse> {
            override fun onResponse(
                call: Call<StatisticsResponse>,
                response: Response<StatisticsResponse>
            ) {
                if (response.isSuccessful) {
                    statisticsData.value = response.body()
                    Log.d(
                        TAG,
                        "Statistics fetched done, sharesOutstanding = ${statisticsData.value?.defaultKeyStatistics?.sharesOutstanding?.value}"
                    )
                } else {
                    Log.d(TAG, "API called rejected with symbol = $symbol")
                    loadingStatisticsStatus.value = LoadingStatus.ERROR
                }
            }

            override fun onFailure(call: Call<StatisticsResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "API called failed: $symbol")
                loadingStatisticsStatus.value = LoadingStatus.ERROR
            }
        })
        ///
    }

    private fun fetchBalanceSheetData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingBalanceSheetStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getBalanceSheet(symbol, region)
            result.enqueue(object : Callback<BalanceSheetResponse> {
                override fun onResponse(
                    call: Call<BalanceSheetResponse>,
                    response: Response<BalanceSheetResponse>
                ) {
                    if (response.isSuccessful) {
                        balanceSheetData.value = response.body()
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")

                        loadingBalanceSheetStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<BalanceSheetResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingBalanceSheetStatus.value = LoadingStatus.ERROR
                }
            })
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
            return
        }
    }

    private fun fetchAnalysisData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingAnalysisStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getAnalysis(symbol, region)
            result.enqueue(object : Callback<AnalysisResponse> {
                override fun onResponse(
                    call: Call<AnalysisResponse>,
                    response: Response<AnalysisResponse>
                ) {
                    if (response.isSuccessful) {
                        analysisData.value = response.body()
                        loadingAnalysisStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")
                        loadingAnalysisStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingAnalysisStatus.value = LoadingStatus.ERROR
                }
            })
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
        }
    }


    private fun shouldExecuteFetch(query: String): Boolean {
        return (!TextUtils.equals(query, currentSymbol)
                || loadingAnalysisStatus.value == LoadingStatus.ERROR
                || loadingBalanceSheetStatus.value == LoadingStatus.ERROR
                || loadingStatisticsStatus.value == LoadingStatus.ERROR
                )
    }


}


