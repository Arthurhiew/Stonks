package com.example.stonks.ui.DetailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.StockDataViewModel
import com.example.stonks.data.LoadingStatus
import com.example.stonks.data.StockData
import com.example.stonks.data.StockSearchItem
import com.example.stonks.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {
    private lateinit var stockSearchItem: StockSearchItem
    private val TAG = StockDetailActivity::class.java.simpleName
    private lateinit var stockDataViewModel: StockDataViewModel
    private var stockData: StockData = StockData()
    private var loadingStatisticsStatus: LoadingStatus = LoadingStatus.LOADING
    private var loadingBalanceSheetStatus: LoadingStatus = LoadingStatus.LOADING
    private var loadingAnalysisStatus: LoadingStatus = LoadingStatus.LOADING
    private var loadingStatus: LoadingStatus = LoadingStatus.LOADING

    //    private var stockData: StockData = StockData(null, null, null, null, null, null, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val PV_Cash_Flow_10yr =
        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)

        val intent = intent
        if (intent != null) {
            stockSearchItem = intent.getSerializableExtra("searchResultItem") as StockSearchItem
            stockDataViewModel.loadStockData(stockSearchItem.symbol, "US")
        }

        // stockdata binding here
        stockDataViewModel.statisticsData.observe(
            this,
            { data ->
                stockData.symbol = stockSearchItem.symbol
                stockData.curOpCashFlow = data.financialData.operatingCashflow.value
                stockData.lastClose = data.summaryDetail.previousClose.value
                stockData.sharesOutstanding = data.defaultKeyStatistics.sharesOutstanding.value
            }
        )

        stockDataViewModel.balanceSheetData.observe(
            this,
            { data ->
                val shortLongTermDebt =
                    data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortLongTermDebt.value
                val longTermDebt =
                    data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].longTermDebt.value
                val cash =
                    data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].cash.value
                val shortTermInvestments =
                    data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments.value
                stockData.totalDebt = shortLongTermDebt + longTermDebt
                stockData.cashNShortTermInvestment = cash + shortTermInvestments
            }
        )

        stockDataViewModel.analysisData.observe(
            this,
            { data ->
                stockData.growthRate = data.earningsTrend.trend[4].growth.value
            }
        )

        stockDataViewModel.loadingAnalysisStatus.observe(
            this,
            { status -> loadingAnalysisStatus = status }
        )

        stockDataViewModel.loadingBalancesheetStatus.observe(
            this,
            { status -> loadingBalanceSheetStatus = status }
        )

        stockDataViewModel.loadingStatisticsStatus.observe(
            this,
            { status -> loadingStatisticsStatus = status }
        )

        while (true) {
            if (loadingAnalysisStatus == LoadingStatus.SUCCESS && loadingBalanceSheetStatus == LoadingStatus.SUCCESS && loadingStatisticsStatus == LoadingStatus.SUCCESS) {

                TODO("hide spinner and show main content")
                break
            }
        }



        binding.valueOpCashflowTv.text = "$11,111 m"
        binding.valueDebtTv.text = "$11,111 m"
        binding.valueInvestmentTv.text = "$11,111 m"
        binding.valueNumShareTv.text = "$11,111 m"
        binding.valueGrowthRate15Tv.text = "$11,111 m"
        binding.valueGrowthRate510Tv.text = "$11,111 m"
        binding.valueGrowthRate1120Tv.text = "$11,111 m"
        binding.valueDiscountRateTv.text = "$11,111 m"
        binding.valueLastCloseTv.text = "$" + " m"
        binding.valueIntrinsicTv.text = "$11,111 m"
        binding.valueVerdictTv.text = "$11,111 m"

    }

    private fun getPV10YrCashFlow(
        curCashFlow: Float,
        growthRate5: Float,
        growthRate10: Float,
        growthRate20: Float,
        discountRate: Float
    ): Float {
        var temp = curCashFlow
        var sum = 0F
        for (i in 0..20) {
            val discountFactor = 1 / (1 + discountRate) * (i + 1)
            //Before Discount
            temp *= when {
                i < 5 -> growthRate5
                i < 10 -> growthRate10
                else -> growthRate20
            }
            //After discount
            sum += temp * discountFactor
        }

        return sum

    }

    private fun getDebtPerShare(totalDebt: Long, numShare: Long): Float {

        return (totalDebt / numShare).toFloat()
    }

    private fun getCashPerShare(totalInvestment: Long, numShare: Long): Float {
        return (totalInvestment / numShare).toFloat()

    }

    private fun getIntrinsicValueBeforeCashOrDebt(PV10YrCashFlow: Float, numShare: Long): Float {
        return PV10YrCashFlow / numShare
    }


    fun getFinalIntrinsicValue(
        debtPerShare: Float,
        cashPerShare: Float,
        intrinsicValueBeforeCashOrDebt: Float
    ): Float {
        return intrinsicValueBeforeCashOrDebt + cashPerShare - debtPerShare

    }


    companion object {
        const val EXTRA_STOCK_DATA = "StockDetailActivity.StockData"
    }
}