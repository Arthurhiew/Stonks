package com.example.stonks.data


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.databinding.StockDataBinding

class StockDataAdapter(
    private val stocks: MutableList<StockData>,
    private val listener: OnItemClickListener,
    private val deleteListener: OnDeleteItemClickListener
) : RecyclerView.Adapter<StockDataAdapter.StockDataViewHolder>() {

    inner class StockDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val binding = StockDataBinding.bind(itemView)
        val symbol : TextView = binding.tvSymbol
        val price : TextView = binding.tvPrice
        val intrinsicValue : TextView = binding.tvIntrinsicValue
        val overUnderValued : TextView = binding.tvOverUnderValued
        private val deleteBtn : ImageView = binding.ivRemoveIcon

        init {
            itemView.setOnClickListener(this)
            deleteBtn.setOnClickListener {
                val position: Int = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteItemClick(position)
                }
            }
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDataViewHolder {
        return StockDataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.stock_data,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StockDataViewHolder, position: Int) {
        val currentStockData = stocks[position]
        holder.symbol.text = currentStockData.symbol
        holder.price.text = currentStockData.lastClose.toString()
        holder.intrinsicValue.text = currentStockData.intrinsicValue.toString()
        if(currentStockData.overUnderValued!! < 0) {
            holder.overUnderValued.text = """${currentStockData.overUnderValued.toString()}%"""
        } else {
            holder.overUnderValued.text = """+${currentStockData.overUnderValued.toString()}%"""
        }
    }

    override fun getItemCount(): Int {
        return stocks.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnDeleteItemClickListener {
        fun onDeleteItemClick(position: Int)
    }
}