package com.example.mobv.ui.viewmodel.data

class PubFriend (
    val user_id: String,
    val user_name: String,
    val pub_id: String?,
    val pub_name: String?,
    val pub_lat: Double?,
    val pub_lon: Double?
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PubFriend

        if (user_id != other.user_id) return false
        if (user_name != other.user_name) return false
        if (pub_id != other.pub_id) return false
        if (pub_name != other.pub_name) return false
        if (pub_lat != other.pub_lat) return false
        if (pub_lon != other.pub_lon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id.hashCode()
        result = 31 * result + user_name.hashCode()
        result = 31 * result + (pub_id?.hashCode() ?: 0)
        result = 31 * result + (pub_name?.hashCode() ?: 0)
        result = 31 * result + (pub_lat?.hashCode() ?: 0)
        result = 31 * result + (pub_lon?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "PubFriend(user_id='$user_id', user_name='$user_name', pub_id=$pub_id, pub_name=$pub_name, pub_lat=$pub_lat, pub_lon=$pub_lon)"
    }


}