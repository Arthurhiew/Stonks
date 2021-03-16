package com.example.stonks.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.R
import com.example.stonks.WatchListViewModel
import com.example.stonks.data.StockData

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var watchListViewModel: WatchListViewModel
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
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        println("HOME FRAG")
        watchListViewModel.insertStockData(StockData("A"))
        watchListViewModel.insertStockData(StockData("B"))
        watchListViewModel.insertStockData(StockData("C"))
        watchListViewModel.insertStockData(StockData("D"))

        watchListViewModel.allStockData!!.observe(viewLifecycleOwner, {
            if (it != null) {
                watchList = it

                print("WATCH LIST DATA:")
                for(data: StockData? in watchList) {
                    println(data)
                }
            }
        })

        return root
    }
}