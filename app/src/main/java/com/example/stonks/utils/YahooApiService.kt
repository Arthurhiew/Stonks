package com.example.stonks.utils

import com.example.stonks.BuildConfig
import com.example.stonks.data.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val API_KEY = BuildConfig.YAHOO_API_KEY
const val API_HOST = "apidojo-yahoo-finance-v1.p.rapidapi.com"

interface YahooApiService {

    @Headers(
        "x-rapidapi-key: $API_KEY",
        "x-rapidapi-host: $API_HOST"
    )
    @GET("/auto-complete")
    fun executeStockSearch(
        @Query("q") query: String,
        @Query("region") region: String,
    ): Call<StockSearchResponse>

    @Headers(
        "x-rapidapi-key: $API_KEY",
        "x-rapidapi-host: $API_HOST"
    )
    @GET("/stock/v2/get-statistics")
    fun getStatistics(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<StatisticsResponse>

    @Headers(
        "x-rapidapi-key: $API_KEY",
        "x-rapidapi-host: $API_HOST"
    )
    @GET("/stock/v2/get-balance-sheet")
    fun getBalanceSheet(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<BalanceSheetResponse>

    @Headers(
        "x-rapidapi-key: $API_KEY",
        "x-rapidapi-host: $API_HOST"
    )
    @GET("/stock/v2/get-analysis")
    fun getAnalysis(
        @Query("symbol") symbol: String,
        @Query("region") region: String,
    ): Call<AnalysisResponse>
}