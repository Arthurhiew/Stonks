package com.example.stonks.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.WatchListViewModel
import com.example.stonks.data.StockData
import com.example.stonks.data.StockDataAdapter
import com.example.stonks.data.StockSearchItem
import com.example.stonks.ui.DetailActivity.StockDetailActivity

class HomeFragment : Fragment(), StockDataAdapter.OnItemClickListener, StockDataAdapter.OnDeleteItemClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var watchListViewModel: WatchListViewModel
    private lateinit var stockDataAdapter: StockDataAdapter
    private lateinit var watchList: List<StockData?>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        watchListViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(WatchListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val rvStockDataItems: RecyclerView = root.findViewById(R.id.rvStockDataItems)

        watchListViewModel.allStockData!!.observe(viewLifecycleOwner, {
            if (it != null) {
                watchList = it
                stockDataAdapter = StockDataAdapter(watchList as MutableList<StockData>, this@HomeFragment, this@HomeFragment)
                rvStockDataItems.adapter = stockDataAdapter
                rvStockDataItems.layoutManager = LinearLayoutManager(context)
            }
        })

        return root
    }

    override fun onItemClick(position: Int) {
        println("Clicked $position")
        val resultItem = watchList[position]
//        Log.d()
        var item = StockSearchItem()
        if (resultItem != null) {
            item.symbol =  resultItem.symbol
        }
        Intent(activity, StockDetailActivity::class.java).putExtra("searchResultItem", item)
            .also { startActivity(it) }
    }

    override fun onDeleteItemClick(position: Int) {
        // println("Trash can clicked $position")
        watchList[position]?.let { watchListViewModel.deleteStockData(it) }
    }
}