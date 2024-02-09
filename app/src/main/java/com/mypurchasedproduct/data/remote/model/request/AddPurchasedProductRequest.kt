package com.mypurchasedproduct.data.remote.model.request

import java.sql.Timestamp

data class AddPurchasedProductRequest(
    private val productId: Long,
    private val count: Int,
    private val unitMeasurementId: Long,
    private val price:Double,
    private val purchaseDate: Long,
)
