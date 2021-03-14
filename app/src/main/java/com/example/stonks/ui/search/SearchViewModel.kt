package com.example.stonks.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stonks.data.LoadingStatus
import com.example.stonks.data.SearchRepository
import com.example.stonks.data.StockSearchItem

class SearchViewModel : ViewModel() {

    private val repository: SearchRepository = SearchRepository()
    var stockSearchResult: LiveData<List<StockSearchItem>> = repository.getStockSearchResults()
    val loadingStatus: LiveData<LoadingStatus> = repository.getLoadingStatus()

    fun executeStockSearch(query: String, region: String) {
        repository.executeStockSearch(query, region)
    }
}