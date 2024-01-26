package com.mypurchasedproduct.presentation.screens.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypurchasedproduct.domain.model.TokenModel
import com.mypurchasedproduct.domain.usecases.PurchasedProductUseCase
import com.mypurchasedproduct.domain.usecases.TokenUseCase
import com.mypurchasedproduct.presentation.state.FindPurchasedProductsState
import com.mypurchasedproduct.presentation.state.HomeState
import com.mypurchasedproduct.presentation.state.AccessTokenItem
import com.mypurchasedproduct.presentation.state.AddPurchasedProductState
import com.mypurchasedproduct.presentation.state.CheckTokenState
import com.mypurchasedproduct.presentation.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val purchasedProductUseCase: PurchasedProductUseCase,
    private val tokenUseCase: TokenUseCase
): ViewModel(){

    private val TAG = this.javaClass.simpleName

    var state by mutableStateOf(HomeState())
        private set

    var checkTokenState by mutableStateOf(CheckTokenState())
        private set


    var accessTokenItem by mutableStateOf(AccessTokenItem())
        private set

    var getPurchasedProductsState by mutableStateOf(FindPurchasedProductsState())
        private set

    var addPurchasedProductState by mutableStateOf(AddPurchasedProductState())
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
                isSignIn = null
            )
            getPurchasedProductsState = FindPurchasedProductsState()
            accessTokenItem = AccessTokenItem()
            checkTokenState = CheckTokenState()
            Log.e(TAG, "[END] SIGN OUT isSignIn ${state.isSignIn}")
        }
    }

    fun getPurchasedProductCurrentUser(offset: Int){
        viewModelScope.async{
            Log.wtf(TAG, "GET PURCHASED PRODUCT CURRENT USER")
            accessTokenItem.accessTokenData?.let{tokenModel->
                val purchasedProducts = this.async { purchasedProductUseCase.getAllPurchasedProductsCurrentUser(tokenModel.id, offset) }.await()
                when(purchasedProducts){
                    is NetworkResult.Success -> {
                        purchasedProducts.data?.let{
                            getPurchasedProductsState = getPurchasedProductsState.copy(
                                isActive = false,
                                purchasedProducts = it,
                                isSuccessResponse = true
                            )
                        }
                    }
                    is NetworkResult.Error ->{
                        getPurchasedProductsState = getPurchasedProductsState.copy(
                            isActive = false,
                            error = purchasedProducts.message
                        )
                    }
                }
            }
        }
    }

    fun onAddPurchasedProductClick(){
        addPurchasedProductState = addPurchasedProductState.copy(
            isActive = true
        )
    }

    fun onCloseAddPurchasedproduct(){
        addPurchasedProductState = addPurchasedProductState.copy(
            isActive = false
        )
    }
}