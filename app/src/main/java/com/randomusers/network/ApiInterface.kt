package com.randomusers.network

import com.randomusers.data.model.UserDetailsModel
import com.randomusers.data.model.WeatherDetailsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("api/?")
    suspend  fun getUsersData(
        @Query(value = "results") results: Int,
        @Query(value = "page") page: Int
    ): Response<UserDetailsModel>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("v1/forecast.json?")
    suspend  fun getWeather(
        @Query(value = "key") key: String,
        @Query(value = "q") latLong: String
    ): Response<WeatherDetailsModel>


}