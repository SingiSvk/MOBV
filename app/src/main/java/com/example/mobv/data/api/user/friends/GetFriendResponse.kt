package com.example.mobv.data.api.user.friends

data class GetFriendResponse (
    val user_id: String,
    val user_name: String,
    val bar_id: String,
    val bar_name: String,
    val bar_lat: Double,
    val bar_lon: Double
)