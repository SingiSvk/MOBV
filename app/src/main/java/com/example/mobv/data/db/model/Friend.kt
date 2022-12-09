package com.example.mobv.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class Friend (
    @PrimaryKey
    val user_id: String,
    val user_name: String,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Friend

        if (user_id != other.user_id) return false
        if (user_name != other.user_name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id.hashCode()
        result = 31 * result + user_name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Friend(user_id='$user_id', user_name='$user_name')"
    }
}