package com.example.vkr.data.repository

import com.example.vkr.domain.utils.Constants
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.infrastructure.ApiClient
import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.IndicesConstituents
import com.finnhub.api.models.Quote

class StocksRepository : IStocksRepository {

    private val apiClient = DefaultApi()

    init {
        ApiClient.apiKey["token"] = Constants.API_KEY
    }

    override suspend fun getIndicesConstituents(indexSymbol: String, data: DataHandler<IndicesConstituents>, error: ErrorHandler) {
        kotlin.runCatching { apiClient.indicesConstituents(indexSymbol) }
            .onSuccess { data(it) }
            .onFailure { error(it.message.toString()) }
    }

    override fun getCompanyProfile(symbol: String, data: DataHandler<CompanyProfile2>, error: ErrorHandler) {
        kotlin.runCatching { apiClient.companyProfile2(symbol, null, null) }
            .onSuccess { data(it) }
            .onFailure { error(it.message.toString()) }
    }

    override fun getQuote(symbol: String, data: DataHandler<Quote>, error: ErrorHandler) {
        kotlin.runCatching { apiClient.quote(symbol) }
            .onSuccess { data(it) }
            .onFailure { error(it.message.toString()) }
    }

    fun test (a:String): Unit {

    }

}