package com.example.mobv.data.api.user.pubs

data class RemoveFromPubRequest (
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double
)