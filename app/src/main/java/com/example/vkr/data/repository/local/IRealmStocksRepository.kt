package com.example.vkr.data.repository.local

import com.example.vkr.data.repository.DataHandler
import com.example.vkr.data.repository.ErrorHandler
import com.example.vkr.data.repository.SuccessHandler
import com.example.vkr.domain.entity.Stock

interface IRealmStocksRepository {
    fun addToFavourite(stock: Stock?, success: SuccessHandler, error: ErrorHandler)
    fun getFavourites(data: DataHandler<List<Stock>>, error: ErrorHandler)
    fun deleteFavourite(stock: Stock?, success: SuccessHandler, error: ErrorHandler)
    fun isStockExist(ticker: String?, data: DataHandler<Boolean>, error: ErrorHandler)
    fun update(stock: Stock?, success: SuccessHandler, error: ErrorHandler)
}