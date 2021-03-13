package com.example.stonks.data

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stonks.BuildConfig
import retrofit2.converter.gson.GsonConverterFactory
import com.example.stonks.utils.YahooApiService
import retrofit2.*

class StockDataRepository {
    private val statisticsData = MutableLiveData<StatisticsResponse>()
    private val balanceSheetData = MutableLiveData<BalanceSheetResponse>()
    private val analysisData = MutableLiveData<AnalysisResponse>()
    private val stockData = MutableLiveData<StockData>()
    private val loadingStatus = MutableLiveData<LoadingStatus>()
    private var currentSymbol: String? = null
    private val yahooApiService: YahooApiService
    private val TAG = StockDataRepository::class.java.simpleName
    private val BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"
    private val API_KEY = BuildConfig.YAHOO_API_KEY

    init {
        loadingStatus.value = LoadingStatus.SUCCESS

        // build retrofit object
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        yahooApiService = retrofit.create(YahooApiService::class.java)
    }

    fun getStockData(symbol: String, region: String): LiveData<StockData> {
        fetchAnalysisData(symbol, region)
        fetchBalanceSheetData(symbol, region)
        fetchStatisticsData(symbol, region)

        val curOpCashFlow = statisticsData.value!!.financialData.operatingCashflow.value
        val lastClose = statisticsData.value!!.summaryDetail.previousClose.value
        val sharesOutstanding = statisticsData.value!!.defaultKeyStatistics.sharesOutstanding.value
        var shortLongTermDebt =
            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortLongTermDebt?.value
        var longTermDebt =
            balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.longTermDebt?.value
        var cash = balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.cash?.value
        var shortTermInvestments = balanceSheetData.value?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortTermInvestments?.value
        var growthRate = analysisData.value?.earningsTrend?.trend?.get(4)?.growth?.value

        stockData.value!!.symbol = symbol
        stockData.value!!.curOpCashFlow = curOpCashFlow
        stockData.value!!.lastClose = lastClose
        stockData.value!!.sharesOutstanding = sharesOutstanding

        if (shortLongTermDebt == null) {
            shortLongTermDebt = 0L
        }

        if (longTermDebt == null) {
            longTermDebt = 0L
        }

        if (cash == null) {
            cash = 0L
        }

        if (shortTermInvestments == null) {
            shortTermInvestments = 0L
        }

        if (growthRate == null) {
            growthRate = 0L
        }

        stockData.value!!.totalDebt = shortLongTermDebt + longTermDebt
        stockData.value!!.cashNShortTermInvestment = cash + shortTermInvestments
        stockData.value!!.growthRate = growthRate
        return stockData
    }

    fun getLoadingStatus(): LiveData<LoadingStatus> {
        return loadingStatus
    }

    private fun fetchStatisticsData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getStatistics(symbol, region)
            result.enqueue(object : Callback<StatisticsResponse> {
                override fun onResponse(
                    call: Call<StatisticsResponse>,
                    response: Response<StatisticsResponse>
                ) {
                    if (response.isSuccessful) {
                        statisticsData.value = response.body()
                        loadingStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")

                        loadingStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<StatisticsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingStatus.value = LoadingStatus.ERROR
                }
            })
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
        }
    }

    private fun fetchBalanceSheetData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getBalanceSheet(symbol, region)
            result.enqueue(object : Callback<BalanceSheetResponse> {
                override fun onResponse(
                    call: Call<BalanceSheetResponse>,
                    response: Response<BalanceSheetResponse>
                ) {
                    if (response.isSuccessful) {
                        balanceSheetData.value = response.body()
                        loadingStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")

                        loadingStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<BalanceSheetResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingStatus.value = LoadingStatus.ERROR
                }
            })
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
        }
    }

    private fun fetchAnalysisData(symbol: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getAnalysis(symbol, region)
            result.enqueue(object : Callback<AnalysisResponse> {
                override fun onResponse(
                    call: Call<AnalysisResponse>,
                    response: Response<AnalysisResponse>
                ) {
                    if (response.isSuccessful) {
                        analysisData.value = response.body()
                        loadingStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")
                        loadingStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingStatus.value = LoadingStatus.ERROR
                }
            })
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
        }
    }


    private fun shouldExecuteFetch(query: String): Boolean {
        return (!TextUtils.equals(query, currentSymbol)
                || loadingStatus.value == LoadingStatus.ERROR)
    }



}


