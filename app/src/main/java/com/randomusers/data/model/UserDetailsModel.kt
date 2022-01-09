package com.randomusers.data.model

import com.google.gson.annotations.SerializedName

data class UserDetailsModel(
    @SerializedName("results") val results: List<Results>
)

data class Results(
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("name") var name: Name? = Name(),
    @SerializedName("location") var location: Location? = Location(),
    @SerializedName("email") var email: String? = null,
    @SerializedName("dob") var dob: Dob? = Dob(),
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("picture") var picture: Picture? = Picture(),
)

data class Name(
    @SerializedName("title") var title: String? = null,
    @SerializedName("first") var first: String? = null,
    @SerializedName("last") var last: String? = null
)

data class Dob(
    @SerializedName("date") var date: String? = null,
    @SerializedName("age") var age: Int? = null
)

data class Picture(
    @SerializedName("large") var large: String? = null,
    @SerializedName("medium") var medium: String? = null,
    @SerializedName("thumbnail") var thumbnail: String? = null

)

data class Coordinates(
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null
)

data class Location(
    @SerializedName("coordinates") var coordinates: Coordinates? = Coordinates(),
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("postcode") var postcode: Any? = null
)