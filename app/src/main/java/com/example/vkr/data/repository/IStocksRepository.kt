package com.example.vkr.data.repository

import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.IndicesConstituents
import com.finnhub.api.models.Quote

typealias ErrorHandler = (String) -> Unit


typealias DataHandler<T> = (T?) -> Unit


typealias SuccessHandler = () -> Unit

interface IStocksRepository {
    suspend fun getIndicesConstituents(
        indexSymbol: String,
        data: DataHandler<IndicesConstituents>,
        error: ErrorHandler
    )

    fun getCompanyProfile(
        symbol: String,
        data: DataHandler<CompanyProfile2>,
        error: ErrorHandler
    )

    fun getQuote(
        symbol: String,
        data: DataHandler<Quote>,
        error: ErrorHandler
    )
}