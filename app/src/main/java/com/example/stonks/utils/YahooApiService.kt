package com.example.stonks.utils

import com.example.stonks.data.StockData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YahooApiService {

    @GET("/stock/v2/get-financials")
    fun getFinancials(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ) : Call<StockData>

}