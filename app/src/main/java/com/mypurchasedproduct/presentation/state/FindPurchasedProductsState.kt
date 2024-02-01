package com.mypurchasedproduct.presentation.state

import com.mypurchasedproduct.data.remote.model.response.PurchasedProductResponse

data class FindPurchasedProductsState (
    val purchasedProducts: List<PurchasedProductResponse> = listOf(),
    val isSuccessResponse: Boolean = false,
    val isLoading: Boolean = false,
    val isActive: Boolean = true,
    val error: String? = null
    )

