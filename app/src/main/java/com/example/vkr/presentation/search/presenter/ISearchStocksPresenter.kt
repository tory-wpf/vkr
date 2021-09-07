package com.example.vkr.presentation.search.presenter

interface ISearchStocksPresenter {
    fun bindFoundedStockList(query: String)
    fun saveSeacrhItem(list: MutableSet<String>)
    fun getSearchItem(): MutableSet<String>?
}