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

class SearchRepository {
    private val TAG = SearchRepository::class.java.simpleName
    private val BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"
    private val yahooApiService: YahooApiService
    private var currentQuery: String? = null
    private val stockSearchResults = MutableLiveData<List<StockSearchItem>>()
    private val loadingStatus = MutableLiveData<LoadingStatus>()

    init {
        loadingStatus.value = LoadingStatus.SUCCESS

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

    fun getStockSearchResults(): LiveData<List<StockSearchItem>> {
        return stockSearchResults
    }

    fun executeStockSearch(query: String, region: String) {
        Log.d(TAG, "Execute new fetch? ${shouldExecuteFetch(query)}")
        if (shouldExecuteFetch(query)) {
            currentQuery = query
            loadingStatus.value = LoadingStatus.LOADING
            val result = yahooApiService.executeStockSearch(query, region)
            result.enqueue(object : Callback<StockSearchResponse> {
                override fun onResponse(
                    call: Call<StockSearchResponse>,
                    response: Response<StockSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        stockSearchResults.value = response.body()?.quotes
                        loadingStatus.value = LoadingStatus.SUCCESS
                    } else {
                        Log.d(TAG, "API called rejected with symbol = $query")
                        loadingStatus.value = LoadingStatus.ERROR
                    }
                }

                override fun onFailure(call: Call<StockSearchResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "API called failed: $query")
                    loadingStatus.value = LoadingStatus.ERROR
                }

            })
        } else {
            Log.d(TAG, "using cached results for this query: $query")

        }

    }


    private fun shouldExecuteFetch(query: String): Boolean {
        return (!TextUtils.equals(query, currentQuery)
                || loadingStatus.value == LoadingStatus.ERROR)
    }
}