package com.example.vkr.presentation.stocks.presenter

import android.os.Handler
import android.os.Looper
import com.example.vkr.R
import com.example.vkr.data.repository.IStocksRepository
import com.example.vkr.data.repository.StocksRepository
import com.example.vkr.data.repository.local.IRealmStocksRepository
import com.example.vkr.data.repository.local.RealmStocksRepository
import com.example.vkr.domain.entity.Stock
import com.example.vkr.presentation.base.BaseStocksPresenter
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter
import com.example.vkr.presentation.stocks.view.IStocksFragment
import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.Quote
import kotlinx.coroutines.*

class StocksPresenter: BaseStocksPresenter<IStocksFragment>(),IStocksPresenter {

    private val repo: IStocksRepository = StocksRepository()
    private val realmRepo: IRealmStocksRepository = RealmStocksRepository()

    companion object {
        val stockList: MutableList<Stock> = mutableListOf()
    }

    override fun bindStockList(indexSymbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.getIndicesConstituents(
                indexSymbol,
                data = {
                    launch {
                        it?.constituents?.sorted()?.forEach {
                            val stock: Stock = Stock(it)
                            stockList.add(stock)
                        }
                        withContext(Dispatchers.Main) {
                            getView()?.bindStocks(stockList as? MutableList<Stock>)
                        }
                        bindStocks()
                    }
                }, error = {
                    Handler(Looper.getMainLooper()).post {
                        getView()?.showReload()
                    }
                })
        }
    }

    private suspend fun bindStocks() {
        for (i in 0 until stockList.size) {
            while (stockList[i].currentPrice == null) {
                val stockPrice: Quote? = getStockPrice(stockList[i].ticker ?: "")
                stockList[i].currentPrice = stockPrice?.c
                stockList[i].previousClosePrice = stockPrice?.pc
            }
            while (stockList[i].name == null) {
                val stockInfo: CompanyProfile2? = getStockInfo(stockList[i].ticker ?: "")
                stockList[i].name = stockInfo?.name
                stockList[i].logo = stockInfo?.logo
            }

            withContext(Dispatchers.Main) {
                realmRepo.isStockExist(stockList[i].ticker,
                    data = {
                        if (it == true) {
                            stockList[i].isFavourite = it == true
                            realmRepo.update(stockList[i], success = {}, error = {})
                            bindFavourites()
                        }
                    }, error = {})
                getView()?.getRVAdapter()?.updateItem(i)
            }
        }
    }

    override fun getStockInfo(symbol: String): CompanyProfile2? {
        var stockInfo: CompanyProfile2? = null
        repo.getCompanyProfile(symbol,
            data = {
                stockInfo = it
            }, error = {})
        return stockInfo
    }

    override fun getStockPrice(symbol: String): Quote? {
        var stockPrice: Quote? = null
        repo.getQuote(symbol,
            data = {
                stockPrice = it
            }, error = {})
        return stockPrice
    }

    override fun bindFavourites() {
        realmRepo.getFavourites(data = {
            val adapter = getView()?.getVPAdapter()
            val stocksFragment =
                adapter?.getItem(1) as IStocksFragment
            stocksFragment.bindStocks(it as? MutableList<Stock>?)
        }, error = {})
    }

    override fun onFavouriteIconClick(
        stock: Stock?,
        holder: StocksRecyclerAdapter.StocksViewHolder
    ) {
        realmRepo.isStockExist(stock?.ticker,
            data = {
                if (it == true)
                    realmRepo.deleteFavourite(stock,
                        success = {
                            val adapter = getView()?.getVPAdapter()
                            holder.changeIcon(R.drawable.ic_no_favourite)
                            val localStocksFragment =
                                adapter?.getItem(1) as IStocksFragment
                            localStocksFragment.updateStocks()
                            val stocksFragment =
                                adapter.getItem(0) as IStocksFragment
                            val stockForUpdate =
                                stockList.filter { it.ticker == stock?.ticker }.first()
                            stockForUpdate.isFavourite = false
                            stocksFragment.getRVAdapter()
                                .updateItem(stockList.indexOf(stockForUpdate))
                        },
                        error = {})
                else
                    realmRepo.addToFavourite(stock,
                        success = {
                            holder.changeIcon(R.drawable.ic_favourite)
                            val localStocksFragment =
                                getView()?.getVPAdapter()?.getItem(1) as IStocksFragment
                            localStocksFragment.updateStocks()
                        }, error = {})
            }, error = {})
    }
}