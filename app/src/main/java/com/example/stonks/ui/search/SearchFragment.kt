package com.example.stonks.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.StockDataViewModel
import com.example.stonks.data.LoadingStatus
import com.example.stonks.data.StockSearchItem
import com.example.stonks.ui.DetailActivity.StockDetailActivity

class SearchFragment : Fragment(), SearchAdapter.OnSearchResultClickListener {

    private lateinit var searchViewModel: SearchViewModel
//    private lateinit var stockDataViewModel: StockDataViewModel

    //    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchboxET: EditText
    private lateinit var searchBtn: Button
    private lateinit var errorMessageTV: TextView
    private lateinit var loadingIndicatorPB: ProgressBar
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchResultsRV: RecyclerView
    private val TAG = SearchFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        binding = FragmentSearchBinding.inflate(inflater, container, false)

//        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        searchResultsRV = root.findViewById(R.id.rv_search_results)
        searchboxET = root.findViewById(R.id.et_search_box)
        searchBtn = root.findViewById(R.id.btn_search)
        errorMessageTV = root.findViewById(R.id.tv_error_message)
        loadingIndicatorPB = root.findViewById(R.id.pb_loading_indicator)
        Log.d(TAG, "Here")
        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        searchResultsRV.setHasFixedSize(true)
        searchAdapter = SearchAdapter(this)
        searchResultsRV.adapter = searchAdapter

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
//        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)

        searchViewModel.stockSearchResult.observe(
            viewLifecycleOwner,
            { stockSearchResult ->
                searchAdapter.updateSearchResults(stockSearchResult)
            }
        )

        searchViewModel.loadingStatus.observe(
            viewLifecycleOwner,
            { loadingStatus ->
                when (loadingStatus) {
                    LoadingStatus.LOADING -> {
                        loadingIndicatorPB.visibility = View.VISIBLE
                        errorMessageTV.visibility = View.GONE
                        searchResultsRV.visibility = View.GONE
                    }
                    LoadingStatus.SUCCESS -> {
                        searchResultsRV.visibility = View.VISIBLE
                        loadingIndicatorPB.visibility = View.GONE
                        errorMessageTV.visibility = View.GONE
                    }
                    else -> {
                        searchResultsRV.visibility = View.GONE
                        loadingIndicatorPB.visibility = View.GONE
                        errorMessageTV.text = "Something wrong with the app you stupid developer"
                        errorMessageTV.visibility = View.VISIBLE
                    }
                }
            }
        )


        // listen to search button clicked
        Log.d(TAG, "before btn clicked")

        root.findViewById<Button>(R.id.btn_search).setOnClickListener {
            val searchQuery = searchboxET.text.toString()
            Log.d(TAG, "Search btn clicked with query = $searchQuery")
            searchViewModel.executeStockSearch(searchQuery, "US")
        }

        return root
    }

    override fun onSearchResultClickListener(resultItem: StockSearchItem) {
        Log.d(TAG, "Search result item clicked, switching to StockDetailActivity by intent")
        Log.d(TAG, resultItem.symbol)
//        stockDataViewModel.getStockData(resultItem.symbol, "US")

        Intent(activity, StockDetailActivity::class.java).putExtra("searchResultItem", resultItem)
            .also { startActivity(it) }


    }


}