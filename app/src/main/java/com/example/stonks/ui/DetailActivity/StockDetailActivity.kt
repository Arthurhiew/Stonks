package com.example.stonks.ui.DetailActivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.StockDataViewModel
import com.example.stonks.data.StockData
import com.example.stonks.data.StockSearchItem
import com.example.stonks.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {
    private lateinit var stockSearchItem: StockSearchItem
    private val TAG = StockDetailActivity::class.java.simpleName
    private lateinit var stockDataViewModel: StockDataViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val PV_Cash_Flow_10yr =
        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)

        val intent = intent
        if (intent != null) {
            stockSearchItem = intent.getSerializableExtra("searchResultItem") as StockSearchItem
            Log.d(TAG, stockSearchItem.symbol)
            stockDataViewModel.getStockData(stockSearchItem.symbol, "US")
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