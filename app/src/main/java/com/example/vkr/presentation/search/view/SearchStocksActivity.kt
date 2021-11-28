package com.example.vkr.presentation.search.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.vkr.R
import com.example.vkr.databinding.ActivitySearchStocksBinding
import com.example.vkr.domain.entity.Stock
import com.example.vkr.domain.utils.Constants.indexSymbol
import com.example.vkr.domain.utils.hide
import com.example.vkr.domain.utils.show
import com.example.vkr.presentation.search.adapter.SearchPopularRequestsAdapter
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter
import com.example.vkr.presentation.search.presenter.ISearchStocksPresenter
import com.example.vkr.presentation.search.presenter.SearchStocksPresenter

class SearchStocksActivity : AppCompatActivity(), StocksRecyclerAdapter.StockClickListener,
    SearchPopularRequestsAdapter.SeacrhRequestsClickListener, ISearchStocksActivity {

    companion object{
        const val REQ_CODE = 9
    }

    private lateinit var binding: ActivitySearchStocksBinding
    private var presenter: ISearchStocksPresenter = SearchStocksPresenter()
    private lateinit var adapter: StocksRecyclerAdapter
    private lateinit var historyAdapter: SearchPopularRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (presenter as SearchStocksPresenter).attachView(this)

        adapter = StocksRecyclerAdapter(null, this)
        binding.stocksSearchList.adapter = adapter

        bindHistoryRequest()
        bindPopularRequest()
        setListeners()
    }

    override fun onDestroy() {
        (presenter as SearchStocksPresenter).detachView()
        super.onDestroy()
    }

    private fun bindHistoryRequest() {
        val historyList = presenter.getSearchItem() ?: mutableSetOf()
        historyAdapter = SearchPopularRequestsAdapter(historyList.toMutableList(), this)
        binding.historyRecyclerView.adapter = historyAdapter

    }

    override fun onBackPressed() {
        if (binding.popularAndHistory.isVisible)
            super.onBackPressed()
        else {
            binding.searchView.setQuery("", false)
            binding.popularAndHistory.show()
            binding.stocksFounded.hide()
        }
    }

    private fun bindPopularRequest(){
        val list = resources.getStringArray(R.array.popular_search_items)
        val searchAdapter = SearchPopularRequestsAdapter(list.toMutableList(), this)
        binding.popularRecyclerView.adapter = searchAdapter
    }

    private fun setListeners() {
        binding.showMoreTextView.setOnClickListener {
            setResult(Activity.RESULT_OK)
            this.finish()
        }
        binding.showMore2TextView.setOnClickListener {
            setResult(Activity.RESULT_OK)
            this.finish()
        }
        setQueryListener()
    }

    private fun setQueryListener(){
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    adapter.clearData()
                    historyAdapter.addItem(it)
                    presenter.bindFoundedStockList(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.stocksFounded.hide()
                    binding.popularAndHistory.show()
                } else {
                    binding.popularAndHistory.hide()
                    binding.stocksFounded.show()
                    presenter.bindFoundedStockList(newText)
                }
                return false
            }
        })
    }

    override fun onFavouriteIconClick(
        stock: Stock?,
        holder: StocksRecyclerAdapter.StocksViewHolder
    ) {
        (presenter as SearchStocksPresenter).onFavouriteIconClick(indexSymbol, stock, holder)
    }

    override fun onSnippetClick() {
        Toast.makeText(this, "Stock!!!", Toast.LENGTH_LONG).show()
    }

    override fun onSearchItemClick(text: String) {
        binding.searchView.setQuery(text, true)
        val historyList = presenter.getSearchItem() ?: mutableSetOf()
        historyList.add(text)
        presenter.saveSeacrhItem(historyList)
    }

    override fun bindSeacrhStocks(list: MutableList<Stock>?) {
        list?.let { adapter.addItems(it) }
    }

}