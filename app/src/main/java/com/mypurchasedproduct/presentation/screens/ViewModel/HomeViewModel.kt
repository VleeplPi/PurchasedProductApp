package com.mypurchasedproduct.presentation.screens.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypurchasedproduct.data.remote.model.response.ProductResponse
import com.mypurchasedproduct.domain.model.TokenModel
import com.mypurchasedproduct.domain.usecases.MeasurementUnitUseCase
import com.mypurchasedproduct.domain.usecases.ProductUseCase
import com.mypurchasedproduct.domain.usecases.PurchasedProductUseCase
import com.mypurchasedproduct.domain.usecases.TokenUseCase
import com.mypurchasedproduct.presentation.state.FindPurchasedProductsState
import com.mypurchasedproduct.presentation.state.HomeState
import com.mypurchasedproduct.presentation.state.AccessTokenItem
import com.mypurchasedproduct.presentation.state.AddPurchasedProductState
import com.mypurchasedproduct.presentation.state.CheckTokenState
import com.mypurchasedproduct.presentation.state.FindMeasurementUnitsState
import com.mypurchasedproduct.presentation.state.FindProductsState
import com.mypurchasedproduct.presentation.ui.item.AddPurchasedProductItem
import com.mypurchasedproduct.presentation.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val purchasedProductUseCase: PurchasedProductUseCase,
    private val tokenUseCase: TokenUseCase,
    private val measurementUnitUseCase: MeasurementUnitUseCase
): ViewModel(){

    private val TAG = this.javaClass.simpleName

    var state by mutableStateOf(HomeState())
        private set

    var checkTokenState by mutableStateOf(CheckTokenState())
        private set

    var totalCosts by mutableStateOf(AtomicInteger(0))
        private set


    var accessTokenItem by mutableStateOf(AccessTokenItem())
        private set

    var getPurchasedProductsState by mutableStateOf(FindPurchasedProductsState())
        private set



    var findMeasurementUnits by mutableStateOf(FindMeasurementUnitsState())
        private set


    init {
        Log.e(TAG, "INIT VIEW MODEL")
    }

    fun checkAccessToken(){
        state = state.copy(isLoading=true)
        Log.wtf(TAG, "CHECK ACCESS TOKEN")
        viewModelScope.launch {
            Log.e(TAG, "[START] VIEW MODEL SCOPE : CHECK ACCESS TOKEN")
            checkTokenState = checkTokenState.copy(
                isActive = true
            )

            tokenUseCase.getAccessToken().take(1).collect{accessToken ->
                if(accessToken != null){
                    Log.wtf(TAG, "ACCESS TOKEN IS EXISTS ${accessToken}")
                    val accessTokenData: TokenModel = tokenUseCase.getAccessTokenData(accessToken)
                    checkTokenState = checkTokenState.copy(
                        isActive = false,
                        isComplete = true,
                    )
                    accessTokenItem = accessTokenItem.copy(
                        accessToken = accessToken,
                        accessTokenData = accessTokenData
                    )
                    state = state.copy(
                        isSignIn = true,
                        isLoading=false
                    )
                }
                else{
                    Log.wtf(TAG, "ACCESS TOKEN IS NOT EXISTS ${accessToken}")
                    checkTokenState = checkTokenState.copy(
                        isActive = false,
                        isComplete = true,
                    )
                    state = state.copy(
                        isSignIn = false,
                        isLoading = false
                    )

                }

            }
            Log.e(TAG, "[FINISH] VIEW MODEL SCOPE : CHECK ACCESS TOKEN")
        }
    }

    fun signOut(){
        state = state.copy(
            isLoading = true
        )
        Log.e(TAG, "[START] SIGN OUT")
        viewModelScope.launch {
            val removedAccessToken = this.async { tokenUseCase.removeAccessToken() }
            removedAccessToken.await()
            state = state.copy(
                isLoading = false,
                isSignIn = null,
                error = null
            )
            getPurchasedProductsState = FindPurchasedProductsState()
            accessTokenItem = AccessTokenItem()
            checkTokenState = CheckTokenState()
            Log.e(TAG, "[END] SIGN OUT isSignIn ${state.isSignIn}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getPurchasedProductCurrentUser(offset: Int){
        getPurchasedProductsState = getPurchasedProductsState.copy(
            isLoading = true
        )
        viewModelScope.launch{
            Log.wtf(TAG, "GET PURCHASED PRODUCT CURRENT USER")
            accessTokenItem.accessTokenData?.let{tokenModel->
                val purchasedProducts = this.async { purchasedProductUseCase.getAllPurchasedProductsCurrentUser(tokenModel.id, offset) }.await()
                when(purchasedProducts){
                    is NetworkResult.Success -> {
                        purchasedProducts.data?.let{
                            this.launch {
                                it.stream().forEach{
                                    totalCosts.addAndGet(it.price.toInt())
                                }
                            }
                            getPurchasedProductsState = getPurchasedProductsState.copy(
                                isActive = false,
                                purchasedProducts = it,
                                isSuccessResponse = true,
                                isLoading = false,
                            )
                        }
                    }
                    is NetworkResult.Error ->{
                        getPurchasedProductsState = getPurchasedProductsState.copy(
                            isActive = false,
                            error = purchasedProducts.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getMeasurementUnits(){
        viewModelScope.launch {
            Log.wtf(TAG, "GET MEASUREMENT UNITS")
            findMeasurementUnits = findMeasurementUnits.copy(
                isLoading = true
            )
            val measurementUnitsResponse = this.async{measurementUnitUseCase.getMeasurementUnits()}.await()
            when(measurementUnitsResponse){
                is NetworkResult.Success ->{
                    findMeasurementUnits = findMeasurementUnits.copy(
                        isLoading = false,
                        isSuccess = true,
                        isUpdating = false,
                        measurementUnits = measurementUnitsResponse.data
                    )
                }
                is NetworkResult.Error -> {
                    findMeasurementUnits = findMeasurementUnits.copy(
                        isLoading = false,
                        isError = true,
                        isUpdating = false,
                        error = measurementUnitsResponse.message
                    )
                }
            }
        }
    }

    fun setGetPurchasedProduct(){
        Log.wtf(TAG, "SET GET PURCHASED PRODUCTS")
        getPurchasedProductsState = getPurchasedProductsState.copy(
            isActive = true
        )
    }



    fun defaultHomeState(){
        state = HomeState()
    }
}