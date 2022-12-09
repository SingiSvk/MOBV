package com.example.mobv.data.db.model

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobv.ui.viewmodel.data.MyLocation

@Entity(tableName = "pubs")
data class Pub(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double,
    var users: Int,
    var dist: Double? = 0.0
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pub

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false
        if (users != other.users) return false
        if (dist != other.dist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        result = 31 * result + users
        result = 31 * result + (dist?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Pub(id='$id', name='$name', type='$type', lat=$lat, lon=$lon, users=$users, dist=$dist)"
    }
}