package com.mypurchasedproduct.presentation.utils

import android.util.Log
import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {
    suspend fun<T> safeApiCall(api: suspend () -> Response<T>): NetworkResult<T>{
        try {
            val response = api()
            if(response.isSuccessful){
                Log.i("SAFE API", "CALL RESPONSE SUCCESSFUL")
                val body = response.body()
                body?. let{
                    return NetworkResult.Success(body)
                } ?: return errorMessage("Body is empty")
            }else{
                val msg: String =ApiResponseUtils.parseMessageFromErrorBody(response.errorBody())
                return errorMessage(msg, response.code())
            }

        }
        catch (e:Exception){
            return errorMessage("Api called failed ${e.message.toString()}")
        }
    }

    private fun <T> errorMessage(errorMessage: String, statusCode: Int? = null): NetworkResult.Error<T>{
        Log.e("BASE API RESPONSE", "ERROR: $errorMessage")
        return NetworkResult.Error(message=errorMessage, statusCode=statusCode)
    }
}