package com.example.stonks.data

import com.google.gson.annotations.SerializedName

data class StatisticsResponse(
    val financialData: FinancialData,
    val summaryDetail: SummaryDetail,
    val defaultKeyStatistics: DefaultKeyStatistics,
    val beta: Beta
)

data class FinancialData(
    val operatingCashflow: OperatingCashFlow
)

data class OperatingCashFlow(
    @SerializedName("raw")
    val value: Long
)

data class SummaryDetail(
    val previousClose: PreviousClose,
    val beta:Beta
)

data class PreviousClose(
    @SerializedName("raw")
    val value: Float
)

data class DefaultKeyStatistics(
    val sharesOutstanding: SharesOutstanding
)

data class SharesOutstanding(
    @SerializedName("raw")
    val value: Long
)

data class Beta(
    @SerializedName("raw")
    val value: Float
)