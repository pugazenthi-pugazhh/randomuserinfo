package com.randomusers.network

import com.randomusers.common.AppUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface ApiServices {
    companion object {
        var retrofitService: ApiInterface? = null
        var retrofitWeatherService: ApiInterface? = null
        fun getInstance(): ApiInterface {
            var okHttpClient: OkHttpClient? = null
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(AppUtil.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                retrofitService = retrofit.create(ApiInterface::class.java)
            }
            return retrofitService!!
        }

        fun getInstanceWeather(): ApiInterface {
            var okHttpClient: OkHttpClient? = null
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            if (retrofitWeatherService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(AppUtil.WEATHER_REPORT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                retrofitWeatherService = retrofit.create(ApiInterface::class.java)
            }
            return retrofitWeatherService!!
        }

    }


}