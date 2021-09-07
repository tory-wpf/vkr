package com.example.vkr.presentation.search.presenter

import android.content.Context
import com.example.vkr.App
import com.example.vkr.R
import com.example.vkr.data.repository.local.IRealmStocksRepository
import com.example.vkr.data.repository.local.RealmStocksRepository
import com.example.vkr.domain.entity.Stock
import com.example.vkr.presentation.base.BaseStocksPresenter
import com.example.vkr.presentation.search.view.ISearchStocksActivity
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter
import com.example.vkr.presentation.stocks.presenter.StocksPresenter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class SearchStocksPresenter: BaseStocksPresenter<ISearchStocksActivity>(), ISearchStocksPresenter {

    private val realmRepo: IRealmStocksRepository = RealmStocksRepository()

    override fun onFavouriteIconClick(
        stock: Stock?,
        holder: StocksRecyclerAdapter.StocksViewHolder
    ) {
        realmRepo.isStockExist(stock?.ticker,
            data = {
                if (it == true)
                    realmRepo.deleteFavourite(stock,
                        success = {
                            holder.changeIcon(R.drawable.ic_no_favourite)
                            StocksPresenter.stockList.filter { it.ticker == stock?.ticker }.first().isFavourite = false
                        },
                        error = {})
                else
                    realmRepo.addToFavourite(stock,
                        success = {
                            StocksPresenter.stockList.filter { it.ticker == stock?.ticker }.first().isFavourite = true
                            holder.changeIcon(R.drawable.ic_favourite)
                        }, error = {})
            }, error = {})
    }

    override fun bindFoundedStockList(query: String) {
        val queryLower = query.toLowerCase(Locale.getDefault())
        val foundedList = StocksPresenter.stockList.filter {
            (it.ticker?.toLowerCase(Locale.getDefault())?.startsWith(queryLower) ?: false) ||
                    (it.name?.toLowerCase(Locale.getDefault())?.startsWith(queryLower) ?: false)
        }
        getView()?.bindSeacrhStocks(foundedList as? MutableList<Stock>)
    }

    override fun saveSeacrhItem(list: MutableSet<String>) {
        val gson = Gson()
        val sp = App.ctx.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
        val editor = sp.edit()
        val json = gson.toJson(list)
        editor.putString("searchHistoryItem", json)
        editor.apply()
    }

    override fun getSearchItem(): MutableSet<String>? {
        val gson = Gson()
        val sp = App.ctx.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
        val jsonString = sp.getString("searchHistoryItem", null)
        val type = object : TypeToken<MutableSet<String>?>() {}.type
        return gson.fromJson(jsonString, type)
    }
}