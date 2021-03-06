package com.example.stonks.data

import java.io.Serializable

data class StockSearchResponse(
    val quotes: List<StockSearchItem>
)

data class StockSearchItem(
    val shortname: String,
    var symbol: String
) : Serializable {
    constructor() : this("", "")
}
