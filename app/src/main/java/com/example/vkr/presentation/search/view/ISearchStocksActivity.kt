package com.example.vkr.presentation.search.view

import com.example.vkr.domain.entity.Stock

interface ISearchStocksActivity {
    fun bindSeacrhStocks(list: MutableList<Stock>?)
}