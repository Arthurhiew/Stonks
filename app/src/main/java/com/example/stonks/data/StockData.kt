package com.example.stonks.data

data class StockData(
    // user input from searchbox
    var symbol:String,

    // getStatistics > financialData > operatingCashflow > raw
    var curOpCashFlow: Long,

    //getStatistics > summaryDetail > previousClose > raw
    var lastClose:Float,

    //getStatistics > defaultKeyStatistics > sharesOutstanding > raw
    var sharesOutstanding: Long,

    // getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> shortLongTermDebt > raw + longTermDebt > raw
    var totalDebt:Long,

    //getBalanceSheet > balanceSheetHistoryQuarterly > balanceSheetStatements[0]> cash + shortTermInvestments
    var cashNShortTermInvestment:Long,

    //getAnalysis > earningsTrend > trend[4] > growth >raw
    var growthRate: Long,
)
