package com.mypurchasedproduct.data.repository

import com.mypurchasedproduct.data.remote.RemoteDataSource
import com.mypurchasedproduct.data.remote.model.request.SignUpRequest
import com.mypurchasedproduct.data.remote.model.response.MessageResponse
import com.mypurchasedproduct.presentation.utils.BaseApiResponse
import com.mypurchasedproduct.presentation.utils.NetworkResult
import javax.inject.Inject

// Сюда же можно добаивть локлаьный DataSource
class PurchasedProductRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse(){
    suspend fun signUp(signUpRequest: SignUpRequest): NetworkResult<MessageResponse>{
        return safeApiCall{remoteDataSource.signUp(signUpRequest)}
    }
}