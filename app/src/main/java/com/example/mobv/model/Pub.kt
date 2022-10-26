package com.example.mobv.model

import com.google.gson.annotations.SerializedName

data class Pub(val type: String, val id: Long, val lat: Float, val lon: Float, val tags: Tags) {
    data class Tags(
        @SerializedName("addr:city")
        val addr_city: String?,
        @SerializedName("addr:country")
        val addr_country: String?,
        @SerializedName("addr:housenumber")
        val addr_housenumber: String?,
        @SerializedName("addr:street")
        val addr_street: String?,
        @SerializedName("addr:streetnumber")
        val addr_streetnumber: String?,
        val amenity: String?,
        val check_date: String?,
        val internet_access: String?,
        @SerializedName("internet_access:fee")
        val internet_access_fee: String?,
        val level: String?,
        val name: String,
        val opening_hours: String?,
        @SerializedName("opening_hours:covid19")
        val opening_hours_covid19: String?,
        val operator: String?,
        val outdoor_seating: String?,
        val payment: String?,
        val shop: String?,
        val source: String?,
        val website: String?
    )
}