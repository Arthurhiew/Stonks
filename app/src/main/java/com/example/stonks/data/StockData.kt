package com.example.stonks.data

import java.io.Serializable

data class StockData(
    // user input from searchbox
    var symbol: String? = "",

    // getStatistics > financialData > operatingCashflow > raw
    var curOpCashFlow: Long? = 0L,

    //getStatistics > summaryDetail > previousClose > raw
    var lastClose: Float? = 0F,

    //getStatistics > defaultKeyStatistics > sharesOutstanding > raw
    var sharesOutstanding: Long? = 0L,

    // getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> shortLongTermDebt > raw + longTermDebt > raw
    var totalDebt: Long? = 0L,

    //getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> cash + shortTermInvestments
    var cashNShortTermInvestment: Long? = 0L,

    //getAnalysis > earningsTrend > trend[4] > growth >raw
    var growthRate: Float? = 0F,
) : Serializable

