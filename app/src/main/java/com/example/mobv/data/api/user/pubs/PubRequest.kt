package com.example.mobv.data.api.user.pubs

data class PubRequest (
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double
)