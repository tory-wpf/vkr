package com.example.vkr.presentation.stocks.presenter

import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.Quote

interface IStocksPresenter {
    fun getDefaultStockList()
    fun bindStockLists()
    fun getStockInfo(symbol: String): CompanyProfile2?
    fun getStockPrice(symbol: String): Quote?
    fun bindFavourites()
}