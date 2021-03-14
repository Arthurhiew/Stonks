package com.example.stonks.data

data class StockSearchResponse(
    val quotes: List<StockSearchItem>
)

data class StockSearchItem(
    val shortname: String,
    val symbol: String
)
