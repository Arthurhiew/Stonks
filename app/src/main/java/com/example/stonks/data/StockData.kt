package com.example.stonks.data

data class StockData(
    // user input from searchbox
    val symbol:String,

    // getStatistics > financialData > operatingCashflow > raw
    val curOpCashFlow: Long,

    //getStatistics > summaryDetail > previousClose > raw
    val lastClose:Float,

    //getStatistics > defaultKeyStatistics > sharesOutstanding > raw
    val sharesOutstanding: Long,

    // getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> shortLongTermDebt > raw + longTermDebt > raw
    val totalDebt:Long,

    //getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> cash + shortTermInvestments
    val cashNShortTInvestment:Long,

    //getAnalysis > earningsTrend > trend[4] > growth >raw
    val growthRate: Long,
)
