package com.example.stonks.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.R
import com.example.stonks.databinding.ActivityMainBinding
import com.example.stonks.databinding.FragmentSearchBinding
import com.example.stonks.utils.StockDataViewModel

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var stockDataViewModel: StockDataViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchboxET: EditText
    private lateinit var searchBtn: Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)


        searchViewModel =
                ViewModelProvider(this).get(SearchViewModel::class.java)
        stockDataViewModel = ViewModelProvider(this).get(StockDataViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)
//        val textView: TextView = root.findViewById(R.id.text_search)
//        searchViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        searchboxET = binding.etSearchBox
        searchBtn = binding.btnSearch
        // listen to search button clicked
        searchBtn.setOnClickListener{
            val searchQuery = searchboxET.text.toString()
            stockDataViewModel.getStockData(searchQuery, "US")
        }

        return root
    }




}