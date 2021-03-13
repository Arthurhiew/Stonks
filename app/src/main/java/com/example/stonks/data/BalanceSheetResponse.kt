package com.example.stonks.data

import com.google.gson.annotations.SerializedName

data class BalanceSheetResponse(
    val balanceSheetHistoryQuarterly: BalanceSheetHistoryQuarterly
)

data class BalanceSheetHistoryQuarterly(
    val balanceSheetStatements: List<BalanceSheetStatements>
)

data class BalanceSheetStatements(
    val shortLongTermDebt: ShortLongTermDebt,
    val longTermDebt: LongTermDebt,
    val cash: Cash,
    val shortTermInvestments: ShortTermInvestments

)

data class ShortTermInvestments(
    @SerializedName("raw")
    val value: Long
)
data class Cash(
    @SerializedName("raw")
    val value: Long
)
data class LongTermDebt(
    @SerializedName("raw")
    val value: Long
)
data class ShortLongTermDebt(
    @SerializedName("raw")
    val value: Long,
)
