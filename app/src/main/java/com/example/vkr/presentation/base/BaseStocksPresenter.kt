package com.example.vkr.presentation.base

import com.example.vkr.domain.entity.Stock
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter

abstract class BaseStocksPresenter<T> : BasePresenter<T>() {
    abstract fun onFavouriteIconClick(stock: Stock?, holder: StocksRecyclerAdapter.StocksViewHolder)
}

