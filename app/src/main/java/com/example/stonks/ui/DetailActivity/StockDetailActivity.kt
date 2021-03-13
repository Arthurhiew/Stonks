package com.example.stonks.ui.DetailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stonks.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.valueOpCashflowTv.text = "$11,111 m"
        binding.valueDebtTv.text = "$11,111 m"
        binding.valueInvestmentTv.text = "$11,111 m"
        binding.valueNumShareTv.text = "$11,111 m"
        binding.valueGrowthRate15Tv.text = "$11,111 m"
        binding.valueGrowthRate510Tv.text = "$11,111 m"
        binding.valueGrowthRate1120Tv.text = "$11,111 m"
        binding.valueDiscountRateTv.text = "$11,111 m"
        binding.valueLastCloseTv.text = "$11,111 m"
        binding.valueIntrinsicTv.text = "$11,111 m"
        binding.valueVerdictTv.text = "$11,111 m"

    }
}