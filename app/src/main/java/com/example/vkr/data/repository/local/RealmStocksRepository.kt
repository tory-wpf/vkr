package com.example.vkr.data.repository.local

import android.util.Log
import com.example.vkr.data.repository.DataHandler
import com.example.vkr.data.repository.ErrorHandler
import com.example.vkr.data.repository.SuccessHandler
import com.example.vkr.domain.entity.Stock
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmStocksRepository : IRealmStocksRepository {
    private val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().allowWritesOnUiThread(true).build()

    private val realm = Realm.getInstance(realmConfig)

    override fun addToFavourite(stock: Stock?, success: SuccessHandler, error: ErrorHandler) {
        realm.executeTransactionAsync({
            stock?.isFavourite = true
            it.copyToRealm(stock)
        }, {
            success()
        }, {
            error(it.message.toString())
        })
    }

    override fun getFavourites(data: DataHandler<List<Stock>>, error: ErrorHandler) {
        var stockList: List<Stock> = listOf()
        realm.executeTransactionAsync({
            val lst = it.where(Stock::class.java).findAll()
            stockList = it.copyFromRealm(lst)
        }, {
            data(stockList)
        }, {
            error(it.message.toString())
        })
    }

    override fun deleteFavourite(stock: Stock?, success: SuccessHandler, error: ErrorHandler) {
        realm.executeTransactionAsync({
            val result = it.where(Stock::class.java).equalTo("ticker", stock?.ticker).findFirst()
            stock?.isFavourite = false
            result?.deleteFromRealm()
        }, {
            success()
        }, {
            error(it.message.toString())
        })
    }

    override fun isStockExistAsync(
        ticker: String?,
        data: DataHandler<Boolean>,
        error: ErrorHandler
    ) {
        var stock: Stock? = null

        realm.executeTransactionAsync({
            stock = it.where(Stock::class.java).equalTo("ticker", ticker).findFirst()
        }, {
            data(stock != null)
        }, {
            error(it.message.toString())
        })
    }

    override fun isStockExist(ticker: String): Boolean {
        var stock: Stock? = null
        realm.executeTransaction {
            stock = it.where(Stock::class.java).equalTo("ticker", ticker).findFirst()
        }
        return stock?.let { true } ?: false
    }

    override fun update(stock: Stock?, success: SuccessHandler, error: ErrorHandler) {
        realm.executeTransactionAsync({
            stock?.isFavourite = true
            it.copyToRealmOrUpdate(stock)
        }, {
            success()
        }, {
            error(it.message.toString())
        })
    }

}