package com.randomusers.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.randomusers.data.model.Results
import com.randomusers.network.ApiServices
import kotlinx.coroutines.*


class UserViewModel : ViewModel() {

    private val userService = ApiServices.getInstance()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val users = MutableLiveData<List<Results>>()
    val usersLoadError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()
    fun loadData(result: Int, page: Int) {
        fetchUsers(result, page)
    }

    private fun fetchUsers(result: Int, page: Int) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = userService.getUsersData(result, page)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    users.value = response.body()?.results
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