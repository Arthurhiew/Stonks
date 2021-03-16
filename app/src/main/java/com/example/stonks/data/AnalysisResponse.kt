package com.example.stonks.data

import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    val earningsTrend: EarningsTrend
)

data class EarningsTrend(
    val trend: List<Trend>
)

data class Trend(
    val growth: Growth
)

data class Growth(
    @SerializedName("raw")
    val value: Float
)