package com.example.stonks.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.data.StockSearchItem
import com.example.stonks.databinding.SearchResultItemBinding

class SearchAdapter(private val resultClickListener: OnSearchResultClickListener) :
    RecyclerView.Adapter<SearchAdapter.SearchResultViewHolder>() {
    private var searchResultsList: List<StockSearchItem>? = null

    interface OnSearchResultClickListener {
        fun onSearchResultClickListener(resultItem: StockSearchItem)
    }

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SearchResultItemBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                val searchResultItem = searchResultsList?.get(adapterPosition)
                if (searchResultItem != null) {
                    resultClickListener.onSearchResultClickListener(searchResultItem)
                }
            }
        }

        val symbolTV = binding.symbolTV
        val shortnameTV = binding.shortnameTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return SearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val currentItem = searchResultsList?.get(position)
        if (currentItem != null) {
            holder.shortnameTV.text = currentItem.shortname
        }
        if (currentItem != null) {
            holder.symbolTV.text = currentItem.symbol
        }
    }

    override fun getItemCount(): Int {
        return searchResultsList?.size ?: 0
    }

    fun updateSearchResults(newResultsList: List<StockSearchItem>) {
        searchResultsList = newResultsList
        notifyDataSetChanged()

    }
}