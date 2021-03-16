package com.example.stonks.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "StockDataRepo")
data class StockData(
    // user input from searchbox
    @ColumnInfo(name = "symbol")
    @NonNull
    @PrimaryKey
    var symbol: String = "",

    // getStatistics > financialData > operatingCashflow > raw
    @Ignore
    var curOpCashFlow: Long? = 0L,

    //getStatistics > summaryDetail > previousClose > raw
    @ColumnInfo(name = "price")
    var lastClose: Float? = 0F,

    //getStatistics > defaultKeyStatistics > sharesOutstanding > raw
    @Ignore
    var sharesOutstanding: Long? = 0L,

    // getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> shortLongTermDebt > raw + longTermDebt > raw
    @Ignore
    var totalDebt: Long? = 0L,

    //getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> cash + shortTermInvestments
    @Ignore
    var cashNShortTermInvestment: Long? = 0L,

    //getAnalysis > earningsTrend > trend[4] > growth >raw
    @Ignore
    var growthRate: Float? = 0F,

    @ColumnInfo(name = "intrinsicValue")
    var intrinsicValue: Float? = 0F,

    @ColumnInfo(name = "overUnderValued")
    var overUnderValued: Float? = 0F
) : Serializable {
    constructor():this(
        symbol = "",
        curOpCashFlow = 0L,
        lastClose = 0F,
        sharesOutstanding = 0L,
        totalDebt = 0L,
        cashNShortTermInvestment = 0L,
        growthRate = 0F,
        intrinsicValue = 0F,
        overUnderValued = 0F
    )
}

