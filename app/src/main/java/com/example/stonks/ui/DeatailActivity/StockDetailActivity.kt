package com.example.stonks.ui.DeatailActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stonks.R
import com.example.stonks.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}