package com.mypurchasedproduct.presentation.screens.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypurchasedproduct.data.remote.model.request.SignInRequest
import com.mypurchasedproduct.domain.usecases.SignInUseCase
import com.mypurchasedproduct.presentation.navigation.PurchasedProductAppRouter
import com.mypurchasedproduct.presentation.navigation.Screen
import com.mypurchasedproduct.presentation.state.SignInState
import com.mypurchasedproduct.presentation.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
): ViewModel(){

    private val TAG = this.javaClass.simpleName

    var state by mutableStateOf(SignInState())
    private set


    init{

    }

    fun toSignIn(username: String, password:String){
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )

            signInUseCase.invoke(SignInRequest(username, password)).let{
                when(it){
                    is NetworkResult.Success ->{

                        it.data?. let {
                            signInUseCase.setAccessTokenStore(it.accessToken)
                            state.copy(
                                responseData = it,
                                isLoading = false,
                                isSignInSuccess = true
                            )
                            PurchasedProductAppRouter.navigateTo(Screen.HomeScreen)
                        } ?: state.copy(
                            isLoading = false,
                            isSignInSuccess = false,
                            error = "токен не найден"
                        )
                    }
                    is NetworkResult.Error ->{
                        state.copy(
                            error = it.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}