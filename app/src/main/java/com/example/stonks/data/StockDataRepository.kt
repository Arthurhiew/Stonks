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
    private val financialData = MutableLiveData<FinancialData>()
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

    fun getFinancialData(): LiveData<FinancialData> {
        return financialData
    }

    fun getLoadingStatus(): LiveData<LoadingStatus> {
        return loadingStatus
    }

    fun fetchFinancialData(symbol: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(symbol)}")
        if (shouldExecuteFetch(symbol)) {
            currentSymbol = symbol
            loadingStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.getFinancials(symbol, "US")
            result.enqueue(object : Callback<FinancialData> {
                override fun onResponse(
                    call: Call<FinancialData>,
                    response: Response<FinancialData>
                ) {
                    if (response.isSuccessful) {
                        financialData.value = response.body()
                        loadingStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $symbol")

                        loadingStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<FinancialData>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $symbol")
                    loadingStatus.value = LoadingStatus.ERROR
                }

            }
            )
        } else {
            Log.d(TAG, "using cached results for this query: $symbol")
        }

    }

    private fun shouldExecuteFetch(query: String): Boolean {
        return (!TextUtils.equals(query, currentSymbol)
                || loadingStatus.value == LoadingStatus.ERROR)
    }

}