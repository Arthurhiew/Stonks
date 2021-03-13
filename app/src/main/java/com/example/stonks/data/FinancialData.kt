package com.example.stonks.data

data class FinancialData(
    //
    val symbol:String,
    // getStatistics financialData > operatingCashflow >raw
    val curOpCashFlow: Float,
    // getBalanceSheet
    //balanceSheetHistoryQuarterly > balanceSheetStatements[0]> shortLongTermDebt + longTermDebt
    val totalDebt:Float,
    //getBalanceSheet
    //balanceSheetHistoryQuarterly > balanceSheetStatements[0]> cash + shortTermInvestments
    val cashNShortTInvestment:Float,
    //getAnalysis
    //earningsTrend > trend[4] > growth >raw
    val growthRate: Float,
    //getStatistics
    //summaryDetail > previousClose > raw
    val lastClose:Float,
    //getStatistics
    //defaultKeyStatistics > sharesOutstanding > raw
    val sharesOutstanding: Float
)
