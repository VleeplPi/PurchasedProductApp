package com.mypurchasedproduct.presentation.state

import com.mypurchasedproduct.data.remote.model.response.PurchasedProductResponse

data class FindPurchasedProductsState (
    val responseData: List<PurchasedProductResponse> = listOf(),
    val isSuccessResponse: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
    )

