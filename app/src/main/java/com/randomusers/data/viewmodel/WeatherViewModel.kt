package com.randomusers.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.randomusers.common.AppUtil
import com.randomusers.network.ApiServices
import com.randomusers.data.model.Current
import kotlinx.coroutines.*

class WeatherViewModel:ViewModel() {
    private val apiInterface = ApiServices.getInstanceWeather()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val users = MutableLiveData<Current>()
    val usersLoadError = MutableLiveData<String?>()
    val weatherDetails = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()
    fun refresh(latlong: String) {
        fetchWeatherDetails(latlong)
    }

    private fun fetchWeatherDetails(latlong:String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.getWeather(AppUtil.API_KEY, latlong)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    users.value = response.body()?.current!!
                    usersLoadError.value = null
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
        usersLoadError.value = ""
        loading.value = false
    }

    private fun onError(message: String) {
        usersLoadError.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}