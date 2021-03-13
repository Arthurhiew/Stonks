package com.example.stonks.utils

import com.example.stonks.data.AnalysisResponse
import com.example.stonks.data.BalanceSheetResponse
import com.example.stonks.data.StatisticsResponse
import com.example.stonks.data.StockData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YahooApiService {

    @GET("/stock/v2/get-statistics")
    fun getStatistics(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<StatisticsResponse>

    @GET("/stock/v2/get-balance-sheet")
    fun getBalanceSheet(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<BalanceSheetResponse>

    @GET("/stock/v2/get-analysis")
    fun getAnalysis(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<AnalysisResponse>


}