package com.example.vkr.presentation.stocks.presenter

import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.Quote

interface IStocksPresenter {
    fun bindStockList(indexSymbol: String)
    fun getStockInfo(symbol: String): CompanyProfile2?
    fun getStockPrice(symbol: String): Quote?
    fun bindFavourites()
}