package com.example.stonks.data

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
    private val balanceSheetData = MutableLiveData<BalanceSheetResponse?>()
    private val analysisData = MutableLiveData<AnalysisResponse>()

    private var loadingStatus = MutableLiveData(0)
    private var currentSymbol: String? = null
    private val yahooApiService: YahooApiService
    private val TAG = StockDataRepository::class.java.simpleName
    private val BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"

    init {
        // build retrofit object
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        yahooApiService = retrofit.create(YahooApiService::class.java)
    }

    fun incrementLoadingStatus() {
        var temp = loadingStatus.value
        if (temp != null) {
            temp = temp.plus(1)
        }
        loadingStatus.value = temp
        Log.d(TAG, "incrementing loading status, new value is: ${loadingStatus.value}")
    }

    fun getLoadingStatus(): LiveData<Int> {
        return loadingStatus
    }

    fun getStatisticsData(): LiveData<StatisticsResponse> {
        return statisticsData
    }

    fun getBalanceSheetData(): MutableLiveData<BalanceSheetResponse?> {
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


    // first fetch
    private fun fetchStatisticsData(symbol: String, region: String) {

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
                    loadingStatus.value?.minus(1)
                }
            }

            override fun onFailure(call: Call<StatisticsResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "API called failed: $symbol")
                loadingStatus.value?.minus(1)

            }
        })
        ///
    }

    private fun fetchBalanceSheetData(symbol: String, region: String) {

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

                    loadingStatus.value?.minus(1)

                }
            }

            override fun onFailure(call: Call<BalanceSheetResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "API called failed: $symbol")
                loadingStatus.value?.minus(1)

            }
        })
    }

    private fun fetchAnalysisData(symbol: String, region: String) {
        currentSymbol = symbol
        val result = yahooApiService.getAnalysis(symbol, region)
        result.enqueue(object : Callback<AnalysisResponse> {
            override fun onResponse(
                call: Call<AnalysisResponse>,
                response: Response<AnalysisResponse>
            ) {
                if (response.isSuccessful) {
                    analysisData.value = response.body()
                } else {
                    Log.d(TAG, "API called rejected with symbol = $symbol")
                    loadingStatus.value?.minus(1)
                }
            }

            override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "API called failed: $symbol")
                loadingStatus.value?.minus(1)

            }
        })
    }

}


