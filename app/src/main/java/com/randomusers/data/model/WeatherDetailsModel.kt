package com.randomusers.data.model

import com.google.gson.annotations.SerializedName

data class WeatherDetailsModel(
    @SerializedName("current") var current: Current? = Current(),
)

data class Current(
    @SerializedName("temp_c") var tempC: Double? = null,
    @SerializedName("temp_f") var tempF: Double? = null,
    @SerializedName("wind_mph") var windMph: Double? = null,
    @SerializedName("wind_kph") var windKph: Double? = null,
    @SerializedName("wind_degree") var windDegree: Int? = null,

    )