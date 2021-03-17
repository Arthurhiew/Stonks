package com.example.stonks.ui.DetailActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val growthRate20: Float = 4.1F + 1F //long term gdp growth rate in the US + 1%
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


        // stockdata binding here
        stockDataViewModel.statisticsData.observe(
            this,
            { data ->

                stockData.symbol = stockSearchItem.symbol
                stockData.curOpCashFlow = data.financialData.operatingCashflow.value
                stockData.lastClose = data.summaryDetail.previousClose.value
                stockData.sharesOutstanding = data.defaultKeyStatistics.sharesOutstanding.value
                stockData.beta = data.summaryDetail.beta.value
                Log.d(TAG, "Beta value : ${stockData.beta}")
            }
        )

        stockDataViewModel.balanceSheetData.observe(
            this,
            { data ->

                var shortTermInvestments = 0L
                if (data != null) {
                    if (data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments != null) {
                        shortTermInvestments =
                            data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments.value

                    }
                }


                var shortLongTermDebt = 0L
                var longTermDebt = 0L
                if (data?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortLongTermDebt?.value != null) {
                    shortLongTermDebt =
                        data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortLongTermDebt.value
                }
                if (data?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.longTermDebt?.value != null) {
                    longTermDebt =
                        data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].longTermDebt.value
                }


                stockData.totalDebt = shortLongTermDebt + longTermDebt

                var cash = 0L
                if (data != null) {
                    if (data.balanceSheetHistoryQuarterly.balanceSheetStatements.get(0).cash != null) {
                        cash =
                            data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].cash.value
                    }
                }
                if (data != null) {
                    if (data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments != null) {
                        shortTermInvestments =
                            data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments.value

                    }
                }
                stockData.cashNShortTermInvestment = cash + shortTermInvestments
                Log.d(
                    TAG,
                    "================== shortLongTermDebt, longTermDebt, cash, ShortTermInvestments: ${shortLongTermDebt}, $longTermDebt, $cash, $shortTermInvestments "
                )
                Log.d(
                    TAG,
                    "------xxxxxxxxxxxxx-------------- totalDebt, cashAndShortTermInvestments: ${stockData.totalDebt}, ${stockData.cashNShortTermInvestment} "
                )

                binding.valueDebtTv.text =
                    String.format("$%d m", stockData.totalDebt!!.div(1000000))

                binding.valueInvestmentTv.text =
                    String.format("$%d m", stockData.cashNShortTermInvestment!!.div(1000000))

            }
        )

        stockDataViewModel.analysisData.observe(
            this,
            { data ->
                stockData.growthRate = data.earningsTrend.trend[4].growth.value
            }
        )

        stockDataViewModel.loadingStatus.observe(
            this,
            { status ->
                if (status == 3) {

                    val growthRate10 = stockData.growthRate!!.div(2)

//        val numShare = stockData.sharesOutstanding

                    val discountRate = getDiscountRate(stockData.beta!!)
                    val pV10YrCashFlow = getPV10YrCashFlow(
                        stockData.curOpCashFlow!!.toFloat(),
                        stockData.growthRate!!, growthRate10, growthRate20, discountRate
                    )

//        val IntrinsicValueBeforeCashOrDebt = getIntrinsicValueBeforeCashOrDebt(growthRate10,numShare!!)

                    val debtPerShare =
                        getDebtPerShare(stockData.totalDebt!!, stockData.sharesOutstanding!!)
                    val cashPerShare =
                        getCashPerShare(
                            stockData.cashNShortTermInvestment!!,
                            stockData.sharesOutstanding!!
                        )

                    val intrinsicValueBeforeCashOrDebt =
                        getIntrinsicValueBeforeCashOrDebt(
                            pV10YrCashFlow,
                            stockData.sharesOutstanding!!
                        )

                    val intrinsicValue =
                        getFinalIntrinsicValue(
                            debtPerShare,
                            cashPerShare,
                            intrinsicValueBeforeCashOrDebt
                        )

                    val verdict = getVerdict(stockData.lastClose!!, intrinsicValue)

                    binding.titleDiscountRateTv.visibility = View.VISIBLE
                    binding.titleIntrinsicTv.visibility = View.VISIBLE
                    binding.titleVerdictTv.visibility = View.VISIBLE

                    binding.valueDiscountRateTv.visibility = View.VISIBLE
                    binding.valueIntrinsicTv.visibility = View.VISIBLE
                    binding.valueVerdictTv.visibility = View.VISIBLE

                    binding.valueDiscountRateTv.text = discountRate.toString() + "%"
                    binding.valueIntrinsicTv.text = "$" + intrinsicValue.toString()
                    binding.valueVerdictTv.text = "$" + verdict

                }
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
                        Log.d(TAG, "analysis loading done")
                        binding.analysisPbLoadingIndicator.visibility = View.GONE

                        setAnalysisDataVisibility(View.VISIBLE)
                        binding.valueOpCashflowTv.text = "$" + String.format(
                            "%d m", stockData.curOpCashFlow!!.div(1000000)
                        )
                        binding.valueLastCloseTv.text = "$" + stockData.lastClose.toString()
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
                        Log.d(TAG, "balancesheet loading done")

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
                        Log.d(TAG, "====== growth rate: ${stockData.growthRate}")

                        binding.statsPbLoadingIndicator.visibility = View.GONE

                        setStatisticsDataVisibility(View.VISIBLE)
                        binding.valueGrowthRate15Tv.text =
                            stockData.growthRate!!.times(100).toString() + "%"

                        binding.valueGrowthRate510Tv.text =
                            stockData.growthRate!!.times(100).div(2).toString() + "%"
                        binding.valueGrowthRate1120Tv.text = "$growthRate20%"
                    }
                    else -> {
                        binding.statsPbLoadingIndicator.visibility = View.GONE

                        setStatisticsDataVisibility(View.GONE)


                    }
                }
            }
        )


//        GlobalScope.launch {
//            while (true) {
//                delay(250L)
//                if (loadingAnalysisStatus == LoadingStatus.SUCCESS && loadingBalanceSheetStatus == LoadingStatus.SUCCESS && loadingStatisticsStatus == LoadingStatus.SUCCESS) {
//                    break
//                }
//            }
//        }


    }


    private fun getVerdict(lastClose: Float, intrinsicValue: Float): Float {


        var premium: Float = intrinsicValue - lastClose
        return premium
    }

    private fun getDiscountRate(
        beta: Float,
        riskFreeRate: Float = 1.44F,
        marKetRiskPremium: Float = 3.42F,

        ): Float {

        var newBeta = beta

        if (beta < 0.8F) {
            newBeta = 0.8F
        } else if (beta > 1.6F) {
            newBeta = 1.6F
        }
        val discountRate: Float = newBeta + riskFreeRate * marKetRiskPremium

        return discountRate
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

    private fun getIntrinsicValueBeforeCashOrDebt(
        PV10YrCashFlow: Float,
        numShare: Long
    ): Float {
        return PV10YrCashFlow / numShare
    }

    private fun getDebtPerShare(totalDebt: Long, numShare: Long): Float {
        Log.d(TAG, "---numShare: $numShare")
        if (numShare == 0L) {
            return 1F
        }
        return (totalDebt / numShare).toFloat()
    }

    private fun getCashPerShare(totalInvestment: Long, numShare: Long): Float {
        if (numShare == 0L)
            return 1F
        return (totalInvestment / numShare).toFloat()

    }


    private fun getFinalIntrinsicValue(
        debtPerShare: Float,
        cashPerShare: Float,
        intrinsicValueBeforeCashOrDebt: Float
    ): Float {
        return intrinsicValueBeforeCashOrDebt + cashPerShare - debtPerShare

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

}

