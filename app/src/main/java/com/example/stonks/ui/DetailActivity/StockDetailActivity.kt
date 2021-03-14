package com.example.stonks.ui.DetailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stonks.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val PV_Cash_Flow_10yr =


        binding.valueOpCashflowTv.text = "$11,111 m"
        binding.valueDebtTv.text = "$11,111 m"
        binding.valueInvestmentTv.text = "$11,111 m"
        binding.valueNumShareTv.text = "$11,111 m"
        binding.valueGrowthRate15Tv.text = "$11,111 m"
        binding.valueGrowthRate510Tv.text = "$11,111 m"
        binding.valueGrowthRate1120Tv.text = "$11,111 m"
        binding.valueDiscountRateTv.text = "$11,111 m"
        binding.valueLastCloseTv.text ="$"+" m"
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
}