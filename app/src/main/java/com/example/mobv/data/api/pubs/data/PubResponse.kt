package com.example.mobv.data.api.pubs.data

data class PubResponse(
    val type: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>
)
