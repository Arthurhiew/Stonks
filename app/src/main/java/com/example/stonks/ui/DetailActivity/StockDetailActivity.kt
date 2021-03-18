package com.example.stonks.ui.DetailActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.StockDataViewModel
import com.example.stonks.WatchListViewModel
import com.example.stonks.data.StockData
import com.example.stonks.data.StockDataDao
import com.example.stonks.data.StockSearchItem
import com.example.stonks.databinding.ActivityStockDetailBinding
import kotlinx.android.synthetic.main.activity_stock_detail.*
import java.io.File
import java.lang.Exception
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

        watchListViewModel = ViewModelProvider(
            this,
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

        var shortTermInvestments = 0L
        var shortLongTermDebt = 0L
        var cash = 0L
        var longTermDebt = 0L
        stockDataViewModel.balanceSheetData.observe(
            this,
            { data ->

//                var shortTermInvestments = 0L
                if (data != null) {
                    if (data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments != null) {
                        shortTermInvestments =
                            data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortTermInvestments.value

                    }
                }


                if (data?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.shortLongTermDebt?.value != null) {
                    shortLongTermDebt =
                        data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].shortLongTermDebt.value
                }
                if (data?.balanceSheetHistoryQuarterly?.balanceSheetStatements?.get(0)?.longTermDebt?.value != null) {
                    longTermDebt =
                        data.balanceSheetHistoryQuarterly.balanceSheetStatements[0].longTermDebt.value
                }


                stockData.totalDebt = shortLongTermDebt + longTermDebt

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

        var debtPerShare: Float = 0F
        var cashPerShare: Float = 0F
        var intrinsicValueBeforeCashOrDebt: Float = 0F
        var pV10YrCashFlow: Float = 0F
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
                    binding.valueGrowthRate1120Tv.text =
                        String.format("%.2f", growthRate20.times(100)) + "%"


                    val growthRate10 = stockData.growthRate!!.div(2)

                    val discountRate = getDiscountRate(stockData.beta!!).div(100)
                    pV10YrCashFlow = getPV10YrCashFlow(
                        stockData.curOpCashFlow!!.toFloat(),
                        stockData.growthRate!!, growthRate10, growthRate20, discountRate
                    )

//                    val pV10YrCashFlow = getPV10YrCashFlow(
//                        80008000000F,
//                        0.1469F, 0.0734F, growthRate20, discountRate
//                    )


                    debtPerShare =
                        getDebtPerShare(stockData.totalDebt!!, stockData.sharesOutstanding!!)
                    cashPerShare =
                        getCashPerShare(
                            stockData.cashNShortTermInvestment!!,
                            stockData.sharesOutstanding!!
                        )

                    intrinsicValueBeforeCashOrDebt =
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

                    binding.symbolTv.text = stockData.symbol



                    binding.valueDiscountRateTv.text =
                        String.format("%.2f", discountRate.times(100)) + "%"
                    if (intrinsicValue < 0) {
                        binding.valueIntrinsicTv.text =
                            "Insufficient data needed to calculate intrinsic value"
//                        binding.valueVerdictTv.visibility = View.VISIBLE
//                        binding.valueVerdictTv.text =  "Insufficient data needed to calculate intrinsic value"
                    } else {
                        stockData.intrinsicValue = String.format("%.2f", intrinsicValue).toFloat()
                        binding.valueIntrinsicTv.text = "$" + String.format("%.2f", intrinsicValue)
                        stockData.overUnderValued = String.format("%.2f", verdict).toFloat()
                        if (verdict > 0) {
                            binding.titleVerdictOvervalueTv.visibility = View.VISIBLE
                            binding.redValueVerdictTv.visibility = View.VISIBLE
                            binding.redValueVerdictTv.text =
                                "+" + String.format("%.2f", verdict) + "%"
                        } else {
                            binding.greenValueVerdictTv.visibility = View.VISIBLE
                            binding.titleVerdictUndervalueTv.visibility = View.VISIBLE
                            binding.greenValueVerdictTv.text = String.format("%.2f", verdict) + "%"
                        }
                    }

//                    if (verdict > 0) {
//                        binding.valueVerdictTv.text = "$+" + String.format("%.2f", verdict)
//                    } else {
//                        binding.valueVerdictTv.text = "$" + String.format("%.2f", verdict)
//                    }

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
            Toast.makeText(applicationContext, "Added", Toast.LENGTH_SHORT).show()
        }

//        1) stock symbol
//        2)current operating cash flow
//        3) last close
//        11) intrinsic value
//        4) outstanding share
//        5) cash
//        6)investment
//        7)cash and short term investment (cash + invesment)
//        8)growth rate yr 1-5
//        9) growth rate yr 6-10
//        10) growth rate yr 11-20
//        12)over/under percentage
//        13)beta
//        14)discount rate
//        15)PV of 10 Yr Cash Flow
//        16) debt per share
//        17)cash per share
//        18) intrinsic value before cash or debt
//        19) final intrinsic value
//        20)long term debt
//        21)shortlongterm debt
//        22)Total debbt (long term debt + shortlongterm debt
        binding.btnExport.setOnClickListener {
            try {
                val out = openFileOutput("""${stockData.symbol}.csv""", Context.MODE_PRIVATE)
//                out.write("symbol, currentOperatingCashFlow, lastClose, outstandingShare, cash, investment, cashNShortTermInvestment, growthRate1-5, growthRate6-10, growthRate11-20, intrinsicValue, overUnderPercentage, beta, discountRate, PV10YrCashFlow, debtPerShare, cashPerShare, intrinsicValueBeforeCashOrDebt, finalIntrinsicValue\n".toByteArray())
                out.write(
                    (
                            "symbol, " +
                                    "last close, " +
                                    "intrinsic value, " +
                                    "current operating cash flow, " +
                                    "outstandingShare, " +
                                    "cash, " +
                                    "short term investment, " +
                                    "cash & short term investment, " +
                                    "growthRate Yr 1-5, " +
                                    "growthRate Yr 6-10, " +
                                    "growthRate Yr 11-20, " +
                                    "over or under percentage, " +
                                    "PV of 10 Yr Cash Flow, " +
                                    "beta, " +
                                    "short term debt, " +
                                    "long term debt, " +
                                    "total debt, " +
                                    "discount rate, " +
                                    "debt per share, " +
                                    "cash per share, " +
                                    "intrinsic value before cash or debt, " +
                                    "\n").toByteArray()
                )

                out.write("""${stockData.symbol}, """.toByteArray())
                out.write("""${stockData.lastClose}, """.toByteArray())
                out.write("""${stockData.intrinsicValue}, """.toByteArray())
                out.write("""${stockData.curOpCashFlow}, """.toByteArray())
                out.write("""${stockData.sharesOutstanding}, """.toByteArray())
                out.write("""$cash ,""".toByteArray())
                out.write("""${shortTermInvestments} ,""".toByteArray())
                out.write("""${stockData.cashNShortTermInvestment}, """.toByteArray())
                out.write("""${binding.valueGrowthRate15Tv.text}, """.toByteArray())
                out.write("""${binding.valueGrowthRate510Tv.text}, """.toByteArray())
                out.write("""${binding.valueGrowthRate1120Tv.text}, """.toByteArray())
                out.write("""${stockData.overUnderValued} ,""".toByteArray())
                out.write("""$pV10YrCashFlow, """.toByteArray())
                out.write("""${stockData.beta},""".toByteArray())
                out.write("""$shortLongTermDebt ,""".toByteArray())
                out.write("""${longTermDebt} ,""".toByteArray())
                out.write("""${stockData.totalDebt}, """.toByteArray())
                out.write("""${binding.valueDiscountRateTv.text} ,""".toByteArray())
                out.write("""$debtPerShare ,""".toByteArray())
                out.write("""$cashPerShare ,""".toByteArray())
                out.write("""$intrinsicValueBeforeCashOrDebt ,""".toByteArray())
                out.close()

                val context = applicationContext
                val fileLocation = File(filesDir, """${stockData.symbol}.csv""")
                val path = FileProvider.getUriForFile(
                    context,
                    "com.example.stonks.fileprovider",
                    fileLocation
                )
                val fileIntent = Intent(Intent.ACTION_SEND)
                fileIntent.type = "text/csv"
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, """${stockData.symbol}""")
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                startActivity(Intent.createChooser(fileIntent, "Send csv"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getVerdict(lastClose: Float, intrinsicValue: Float): Float {
        return ((lastClose - intrinsicValue) / intrinsicValue) * 100
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
                i < 5 -> growthRate5 + 1
                i < 10 -> growthRate10 + 1
                else -> growthRate20 + 1
            }
            val discountFactor = (1 / (1 + discountRate)).pow(i + 1)

            projectedCashFlow *= growthR
            val discountedValue = projectedCashFlow * discountFactor

            sum += discountedValue
            Log.d(
                TAG,
                "${i}: discountedValue: ${projectedCashFlow}, discountFactor: ${discountFactor}, sum: ${sum}, growthR: ${growthR}"
            )
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

