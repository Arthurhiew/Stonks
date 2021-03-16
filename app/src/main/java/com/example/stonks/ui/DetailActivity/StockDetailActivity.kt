package com.example.stonks.ui.DetailActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
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
    private lateinit var binding: ActivityStockDetailBinding

    //    private var stockData: StockData = StockData(null, null, null, null, null, null, null)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val PV_Cash_Flow_10yr =
        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)

        val intent = intent
        if (intent != null) {
            stockSearchItem = intent.getSerializableExtra("searchResultItem") as StockSearchItem
            stockDataViewModel.loadStockData(stockSearchItem.symbol, "US")
        }
        setDetailPageVisibility(View.GONE)
        binding.titleDiscountRateTv.visibility = View.GONE
        binding.titleInvestmentTv.visibility = View.GONE
        binding.titleVerdictTv.visibility = View.GONE
        binding.valueDiscountRateTv.visibility = View.GONE
        binding.valueInvestmentTv.visibility = View.GONE
        binding.valueVerdictTv.visibility = View.GONE

        var isStatisticDataDone = false
        var isBalanceSheetDataDone = false
        var isAnalysisDataDone = false
        // stockdata binding here
        stockDataViewModel.statisticsData.observe(
            this,
            { data ->

                stockData.symbol = stockSearchItem.symbol
                stockData.curOpCashFlow = data.financialData.operatingCashflow.value
                stockData.lastClose = data.summaryDetail.previousClose.value
                stockData.sharesOutstanding = data.defaultKeyStatistics.sharesOutstanding.value
                isStatisticDataDone = true
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
//                val shortTermInvestments =
//                    data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments.value
                Log.d(TAG, "fgkfhdjskohfjds hafusdahfkjsdfhjkdshfjahfds $shortLongTermDebt, $longTermDebt, $cash, ")
                stockData.totalDebt = shortLongTermDebt + longTermDebt
                stockData.cashNShortTermInvestment = cash
//                + shortTermInvestments

                isBalanceSheetDataDone = true
            }
        )

        stockDataViewModel.analysisData.observe(
            this,
            { data ->
                stockData.growthRate = data.earningsTrend.trend[4].growth.value
                isAnalysisDataDone = true
            }
        )

        stockDataViewModel.loadingAnalysisStatus.observe(
            this,
            { status ->
                loadingAnalysisStatus = status
                when (loadingAnalysisStatus) {
                    LoadingStatus.LOADING -> {
                        binding.analysisPbLoadingIndicator.visibility = View.VISIBLE

                        setAnalysisDataVisibility(View.GONE)

                    }
                    LoadingStatus.SUCCESS -> {
                        binding.analysisPbLoadingIndicator.visibility = View.GONE

                        setAnalysisDataVisibility(View.VISIBLE)
                        binding.valueOpCashflowTv.text = "$" + String.format(
                            "%d m", stockData.curOpCashFlow!!.div(1000000)
                        )
                        binding.valueLastCloseTv.text = "$"+ stockData.lastClose.toString()
                        binding.valueNumShareTv.text = "$" + String.format(
                            "%d m", stockData.sharesOutstanding!!.div(1000000)
                        )

                    }
                    else -> {
                        binding.analysisPbLoadingIndicator.visibility = View.GONE

                        setAnalysisDataVisibility(View.GONE)


                    }
                }
            }
        )

        stockDataViewModel.loadingBalancesheetStatus.observe(
            this,
            { status ->
                loadingBalanceSheetStatus = status

                when (loadingBalanceSheetStatus) {
                    LoadingStatus.LOADING -> {
                        binding.balsheetPbLoadingIndicator.visibility = View.VISIBLE

                        setBalanceSheetDataVisibility(View.GONE)

                    }
                    LoadingStatus.SUCCESS -> {
                        binding.balsheetPbLoadingIndicator.visibility = View.GONE

                        setBalanceSheetDataVisibility(View.VISIBLE)
                        binding.valueInvestmentTv.text = "$" +
                                String.format(
                                    "%d m", stockData.cashNShortTermInvestment!!.div(1000000)
                                )




                        binding.valueDebtTv.text = "$" + String.format(
                            "%d m", stockData.totalDebt!!.div(1000000)
                        )
                    }
                    else -> {
                        binding.balsheetPbLoadingIndicator.visibility = View.GONE

                        setBalanceSheetDataVisibility(View.GONE)


                    }
                }
            }
        )

        stockDataViewModel.loadingStatisticsStatus.observe(
            this,
            { status ->
                loadingStatisticsStatus = status
                when (loadingStatisticsStatus) {
                    LoadingStatus.LOADING -> {
                        binding.statsPbLoadingIndicator.visibility = View.VISIBLE

                        setStatisticsDataVisibility(View.GONE)

                    }
                    LoadingStatus.SUCCESS -> {
                        binding.statsPbLoadingIndicator.visibility = View.GONE

                        setStatisticsDataVisibility(View.VISIBLE)
                        binding.valueGrowthRate15Tv.text = "$" + stockData.growthRate.toString()
                        binding.valueGrowthRate510Tv.text =
                            "$" + (stockData.growthRate?.div(2))
                        binding.valueGrowthRate1120Tv.text = "$"
                    }
                    else -> {
                        binding.statsPbLoadingIndicator.visibility = View.GONE

                        setStatisticsDataVisibility(View.GONE)


                    }
                }
            }
        )

//        if (isAnalysisDataDone && isBalanceSheetDataDone && isStatisticDataDone) {
//            loadingAnalysisStatus = LoadingStatus.SUCCESS
//            loadingBalanceSheetStatus = LoadingStatus.SUCCESS
//            loadingStatisticsStatus = LoadingStatus.SUCCESS
//            binding.detailPbLoadingIndicator.visibility = View.GONE
////
//        }
//        while (true) {
//            if (loadingAnalysisStatus == LoadingStatus.SUCCESS && loadingBalanceSheetStatus == LoadingStatus.SUCCESS && loadingStatisticsStatus == LoadingStatus.SUCCESS) {
//
//                TODO("hide spinner and show main content")
//
//
//                break
//            }
//        }
        //analysis Data
//        binding.valueOpCashflowTv.text = "$" + stockData.curOpCashFlow.toString()
//        binding.valueLastCloseTv.text = "$"
//        binding.valueNumShareTv.text = "$" + stockData.sharesOutstanding.toString()

        //balance sheet
//        binding.valueInvestmentTv.text = "$" + stockData.cashNShortTermInvestment.toString()
//        binding.valueDebtTv.text = "$" + stockData.totalDebt.toString()

        //statistics
//        binding.valueGrowthRate15Tv.text = "$" + stockData.growthRate.toString()
//        binding.valueGrowthRate510Tv.text = "$" + (stockData.growthRate?.div(2)).toString()
//        binding.valueGrowthRate1120Tv.text = "$"

        if (loadingAnalysisStatus == LoadingStatus.SUCCESS && loadingBalanceSheetStatus == LoadingStatus.SUCCESS && loadingStatisticsStatus == LoadingStatus.SUCCESS) {
            binding.titleDiscountRateTv.visibility = View.VISIBLE
            binding.titleInvestmentTv.visibility = View.VISIBLE
            binding.titleVerdictTv.visibility = View.VISIBLE

            binding.valueDiscountRateTv.visibility = View.VISIBLE
            binding.valueInvestmentTv.visibility = View.VISIBLE
            binding.valueVerdictTv.visibility = View.VISIBLE

            binding.valueDiscountRateTv.text = "$"
            binding.valueIntrinsicTv.text = "$"
            binding.valueVerdictTv.text = "$"

        }
        //final

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

    private fun setAnalysisDataVisibility(status: Int) {
        binding.titleOpCashflowTv.visibility = status
        binding.valueOpCashflowTv.visibility = status

        binding.titleLastCloseTv.visibility = status
        binding.valueLastCloseTv.visibility = status

        binding.titleNumShareTv.visibility = status
        binding.valueNumShareTv.visibility = status
    }

    private fun setBalanceSheetDataVisibility(status: Int) {
        binding.titleInvestmentTv.visibility = status
        binding.valueInvestmentTv.visibility = status

        binding.titleDebtTv.visibility = status
        binding.valueDebtTv.visibility = status
    }

    private fun setStatisticsDataVisibility(status: Int) {
        binding.titleGrowthRate15Tv.visibility = status
        binding.titleGrowthRate510Tv.visibility = status
        binding.titleGrowthRate1120Tv.visibility = status

        binding.valueGrowthRate15Tv.visibility = status
        binding.valueGrowthRate510Tv.visibility = status
        binding.valueGrowthRate1120Tv.visibility = status

    }

    private fun setDetailPageVisibility(status: Int) {
        binding.valueVerdictTv.visibility = status
        binding.valueIntrinsicTv.visibility = status
        binding.valueLastCloseTv.visibility = status
        binding.valueDiscountRateTv.visibility = status
        binding.valueGrowthRate15Tv.visibility = status
        binding.valueGrowthRate510Tv.visibility = status
        binding.valueGrowthRate1120Tv.visibility = status
        binding.valueNumShareTv.visibility = status
        binding.valueInvestmentTv.visibility = status
        binding.valueDebtTv.visibility = status
        binding.valueOpCashflowTv.visibility = status


        binding.titleDebtTv.visibility = status
        binding.titleGrowthRate15Tv.visibility = status
        binding.titleGrowthRate510Tv.visibility = status
        binding.titleGrowthRate1120Tv.visibility = status
        binding.titleIntrinsicTv.visibility = status
        binding.titleInvestmentTv.visibility = status
        binding.titleLastCloseTv.visibility = status
        binding.titleNumShareTv.visibility = status
        binding.titleDiscountRateTv.visibility = status
        binding.titleOpCashflowTv.visibility = status
        binding.titleVerdictTv.visibility = status
    }

    private fun getDebtPerShare(totalDebt: Long, numShare: Long): Float {

        return (totalDebt / numShare).toFloat()
    }

    private fun getCashPerShare(totalInvestment: Long, numShare: Long): Float {
        return (totalInvestment / numShare).toFloat()

    }

    private fun getIntrinsicValueBeforeCashOrDebt(
        PV10YrCashFlow: Float,
        numShare: Long
    ): Float {
        return PV10YrCashFlow / numShare
    }


    fun getFinalIntrinsicValue(
        debtPerShare: Float,
        cashPerShare: Float,
        intrinsicValueBeforeCashOrDebt: Float
    ): Float {
        return intrinsicValueBeforeCashOrDebt + cashPerShare - debtPerShare

    }
}

