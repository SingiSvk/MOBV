package com.example.mobv.data.api.user.pubs

class GetPubsResponse(
    val bar_id: String,
    val bar_name: String,
    val bar_type: String,
    val lat: Double,
    var lon: Double,
    var users: Int
)