package com.example.stonks.ui.DetailActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.StockDataViewModel
import com.example.stonks.WatchListViewModel
import com.example.stonks.data.StockData
import com.example.stonks.data.StockDataDao
import com.example.stonks.data.StockSearchItem
import com.example.stonks.databinding.ActivityStockDetailBinding
import kotlin.math.pow

class StockDetailActivity : AppCompatActivity() {
    private lateinit var stockSearchItem: StockSearchItem
    private val TAG = StockDetailActivity::class.java.simpleName
    private lateinit var stockDataViewModel: StockDataViewModel
    private var stockData: StockData = StockData()
    private lateinit var watchListViewModel: WatchListViewModel
    private lateinit var mainContentCL: ConstraintLayout
    private lateinit var loadingPb: ProgressBar
    private val growthRate20: Float = 0.041F + 0.01F //long term gdp growth rate in the US + 1%
    private lateinit var binding: ActivityStockDetailBinding

    //    private var stockData: StockData = StockData(null, null, null, null, null, null, null)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        watchListViewModel =  ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(WatchListViewModel::class.java)

//        val PV_Cash_Flow_10yr =
        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)

        val intent = intent
        if (intent != null) {
            stockSearchItem = intent.getSerializableExtra("searchResultItem") as StockSearchItem
            stockDataViewModel.loadStockData(stockSearchItem.symbol, "US")
        }
        mainContentCL = binding.mainContentCl
        loadingPb = binding.spinnerPb
        loadingPb.visibility = View.VISIBLE
        loadingPb.visibility = View.GONE
        // stockdata binding here
        stockDataViewModel.statisticsData.observe(
            this,
            { data ->

                stockData.symbol = stockSearchItem.symbol
                stockData.curOpCashFlow = data.financialData.operatingCashflow.value
                stockData.lastClose = data.summaryDetail.previousClose.value
                stockData.sharesOutstanding = data.defaultKeyStatistics.sharesOutstanding.value
                stockData.beta = data.summaryDetail.beta.value
                stockDataViewModel.incrementLoadingStatus()
                Log.d(
                    TAG,
                    "Done with statistics, loadingStatus = ${stockDataViewModel.getLoadingStatus().value}"
                )

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



                stockDataViewModel.incrementLoadingStatus()
                Log.d(
                    TAG,
                    "Done with balancesheet, loadingStatus = ${stockDataViewModel.getLoadingStatus().value}"
                )


            }
        )

        stockDataViewModel.analysisData.observe(
            this,
            { data ->
                stockData.growthRate = data.earningsTrend.trend[4].growth.value
                stockDataViewModel.incrementLoadingStatus()
                Log.d(
                    TAG,
                    "Done with Analysis, loadingStatus = ${stockDataViewModel.getLoadingStatus().value}"
                )


            }
        )



        stockDataViewModel.loadingStatus.observe(
            this,
            { status ->
//                if all data is loaded
                if (status == 3) {
                    Log.d(TAG, "status == 3")

                    binding.valueOpCashflowTv.text = "$" + String.format(
                        "%d m", stockData.curOpCashFlow!!.div(1000000)
                    )
                    binding.valueLastCloseTv.text = "$" + stockData.lastClose.toString()
                    binding.valueNumShareTv.text = "$" + String.format(
                        "%d m", stockData.sharesOutstanding!!.div(1000000)
                    )

                    binding.valueInvestmentTv.text = "$" +
                            String.format(
                                "%d m", stockData.cashNShortTermInvestment!!.div(1000000)
                            )
                    binding.valueDebtTv.text = "$" + String.format(
                        "%d m", stockData.totalDebt!!.div(1000000)
                    )

                    binding.valueGrowthRate15Tv.text =
                        String.format("%.2f", stockData.growthRate!!.times(100)) + "%"

                    binding.valueGrowthRate510Tv.text =
                        String.format("%.2f", stockData.growthRate!!.times(100).div(2)) + "%"
                    binding.valueGrowthRate1120Tv.text = String.format("%.2f", growthRate20.times(100)) + "%"


                    val growthRate10 = stockData.growthRate!!.div(2)

                    val discountRate = getDiscountRate(stockData.beta!!).div(100)
                    val pV10YrCashFlow = getPV10YrCashFlow(
                        stockData.curOpCashFlow!!.toFloat(),
                        stockData.growthRate!!, growthRate10, growthRate20, discountRate
                    )

//                    val pV10YrCashFlow = getPV10YrCashFlow(
//                        80008000000F,
//                        0.1469F, 0.0734F, growthRate20, discountRate
//                    )


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

                    Log.d(
                        TAG,
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx discountRate:${discountRate}, pV10:${pV10YrCashFlow}, debtPS:${debtPerShare}, cashPS:${cashPerShare}, Ins before cash/debt:${intrinsicValueBeforeCashOrDebt}, final:${intrinsicValue}"
                    )
                    Log.d(TAG, "cccccccccccc")


                    binding.valueDiscountRateTv.text =
                        String.format("%.2f", discountRate.times(100)) + "%"
                    binding.valueIntrinsicTv.text = "$" + String.format("%.2f", intrinsicValue)

                    if (verdict > 0) {
                        binding.valueVerdictTv.text = "$+" + String.format("%.2f", verdict)
                    } else {
                        binding.valueVerdictTv.text = "$" + String.format("%.2f", verdict)
                    }

                    // All calculations have been done

                    Log.d(
                        TAG,
                        "opcashflow: ${stockData.curOpCashFlow}, cashInvestments: ${stockData.cashNShortTermInvestment}, totalDebt: ${stockData.totalDebt}, numShares: ${stockData.sharesOutstanding}, growth rate: ${stockData.growthRate}"
                    )

                    loadingPb.visibility = View.GONE
                    mainContentCL.visibility = View.VISIBLE

                } else if (status < 3) {
                    Log.d(TAG, "status < 3")
                    loadingPb.visibility = View.VISIBLE
                    mainContentCL.visibility = View.GONE
                }
            }
        )

        binding.btnAddToWatchList.setOnClickListener {
            watchListViewModel.insertStockData(stockData)
        }
    }


    private fun getVerdict(lastClose: Float, intrinsicValue: Float): Float {
        return lastClose - intrinsicValue
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

        return newBeta + riskFreeRate * marKetRiskPremium
    }

    private fun getPV10YrCashFlow(
        curCashFlow: Float,
        growthRate5: Float,
        growthRate10: Float,
        growthRate20: Float,
        discountRate: Float
    ): Float {
        var projectedCashFlow = curCashFlow
        var sum = 0F

        for (i in 0..20) {
            val growthR = when {
                i < 5 -> growthRate5 +1
                i < 10 -> growthRate10+1
                else -> growthRate20+1
            }
            val discountFactor = (1 / (1 + discountRate)).pow(i + 1)

            projectedCashFlow*= growthR
            val discountedValue = projectedCashFlow *discountFactor

            sum += discountedValue
            Log.d(TAG, "${i}: discountedValue: ${projectedCashFlow}, discountFactor: ${discountFactor}, sum: ${sum}, growthR: ${growthR}")
        }

        return sum

    }

    private fun getIntrinsicValueBeforeCashOrDebt(
        PV10YrCashFlow: Float,
        numShare: Long
    ): Float {
        return PV10YrCashFlow / numShare.toFloat()
    }

    private fun getDebtPerShare(totalDebt: Long, numShare: Long): Float {

        return (totalDebt.toFloat().div(numShare.toFloat()))
    }

    private fun getCashPerShare(totalInvestment: Long, numShare: Long): Float {

        return (totalInvestment.toFloat().div(numShare.toFloat()))

    }


    private fun getFinalIntrinsicValue(
        debtPerShare: Float,
        cashPerShare: Float,
        intrinsicValueBeforeCashOrDebt: Float
    ): Float {
        return intrinsicValueBeforeCashOrDebt + cashPerShare - debtPerShare

    }


}

