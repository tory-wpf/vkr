package com.example.vkr.domain.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode

open class Stock(
    @PrimaryKey var ticker: String? = null,
    var logo: String? = null,
    var name: String? = null,
    var currentPrice: Float? = null,
    var previousClosePrice: Float? = null,
    var isFavourite: Boolean = false
) : RealmObject() {
    fun cutDelta(): String {
        val delta = currentPrice?.minus(previousClosePrice ?: 1f)?.toDouble() ?:0.0
        val deltaPersent = delta*100.div(previousClosePrice ?: 1f).toDouble()
        val deltaBD = BigDecimal(delta).setScale(2, RoundingMode.HALF_UP)
        val deltaPersenBD = BigDecimal(deltaPersent).setScale(2, RoundingMode.HALF_UP).abs()
        var str = ""
        if (deltaBD != null) {
            str = if (deltaBD.toDouble() > 0.toDouble()) {
                "+$deltaBD$ ($deltaPersenBD%)"
            } else {
                "$deltaBD$ ($deltaPersenBD%)"
            }
        }
        return str
    }
}
