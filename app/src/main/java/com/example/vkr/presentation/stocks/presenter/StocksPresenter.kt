package com.example.vkr.presentation.stocks.presenter

import android.os.Handler
import android.os.Looper
import android.util.Log
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

class StocksPresenter : BaseStocksPresenter<IStocksFragment>(), IStocksPresenter {

    private val repo: IStocksRepository = StocksRepository()
    private val realmRepo: IRealmStocksRepository = RealmStocksRepository()

    companion object {
        val stockListNDX: MutableList<Stock> = mutableListOf()
        val stockListGSPC: MutableList<Stock> = mutableListOf()
        val stockListDJI: MutableList<Stock> = mutableListOf()
        var currentIndex = "NDX"
    }

    override fun getDefaultStockList() {
        getView()?.bindStocks(stockListNDX as? MutableList<Stock>)
    }

    override fun bindStockLists() {
        bindStockList("^NDX", stockListNDX)
        bindStockList("^GSPC", stockListGSPC)
        bindStockList("^DJI", stockListDJI)
    }

    private fun bindStockList(indexSymbol: String, list: MutableList<Stock>) {
        CoroutineScope(Dispatchers.IO).launch() {
            repo.getIndicesConstituents(
                indexSymbol,
                data = {
                    launch {
                        it?.constituents?.sorted()?.forEach {
                            val stock: Stock = Stock(it)
                            withContext(Dispatchers.Main) {
                                if (realmRepo.isStockExist(it))
                                    stock.isFavourite = true
                            }
                            list.add(stock)
                        }
                        bindStocks(indexSymbol, list)
                    }
                }, error = {
                    Handler(Looper.getMainLooper()).post {
                        getView()?.showReload()
                    }
                })
        }
    }

    private suspend fun bindStocks(indexSymbol: String, stockList: MutableList<Stock>) {

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
                realmRepo.isStockExistAsync(stockList[i].ticker,
                    data = {
                        if (it == true) {
                            stockList[i].isFavourite = it == true
                            realmRepo.update(stockList[i], success = {}, error = {})
                            bindFavourites()
                        }
                    }, error = {})
                if (currentIndex == indexSymbol)
                    getView()?.getRVAdapter()?.updateItem(i, stockList)
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
        indexSymbol: String,
        stock: Stock?,
        holder: StocksRecyclerAdapter.StocksViewHolder
    ) {
        realmRepo.isStockExistAsync(stock?.ticker,
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
                            val lists = stockListDJI + stockListGSPC + stockListNDX
                            val stockForUpdate =
                                lists.filter { it.ticker == stock?.ticker }.firstOrNull()
                            stockForUpdate?.isFavourite = false

                            val listDJI =
                                if (stockListDJI.indexOf(stockForUpdate) == -1) null else stockListDJI
                            val listGSPC =
                                if (stockListGSPC.indexOf(stockForUpdate) == -1) null else stockListGSPC
                            val listNDX =
                                if (stockListNDX.indexOf(stockForUpdate) == -1) null else stockListNDX

                            var stockIndex = -1
                            var stockList = mutableListOf<Stock>()
                            when {
                                listDJI != null -> {
                                    stockList = stockListDJI
                                    stockIndex = stockListDJI.indexOf(stockForUpdate)
                                }
                                listGSPC != null -> {
                                    stockList = stockListGSPC
                                    stockIndex = stockListGSPC.indexOf(stockForUpdate)
                                }
                                listNDX != null -> {
                                    stockList = stockListNDX
                                    stockIndex = stockListNDX.indexOf(stockForUpdate)
                                }
                            }
                            if (indexSymbol == currentIndex)
                                stocksFragment.getRVAdapter()
                                    .updateItem(stockIndex, stockList)
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