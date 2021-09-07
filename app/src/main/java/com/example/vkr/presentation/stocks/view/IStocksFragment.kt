package com.example.vkr.presentation.stocks.view

import com.example.vkr.domain.entity.Stock
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter
import com.example.vkr.presentation.stocks.adapters.StocksViewPagerAdapter

interface IStocksFragment {
    fun showReload()
    fun getVPAdapter(): StocksViewPagerAdapter
    fun bindStocks(data: MutableList<Stock>?)
    fun bindStock(item: Stock?)
    fun updateStocks()
    fun getRVAdapter(): StocksRecyclerAdapter
}