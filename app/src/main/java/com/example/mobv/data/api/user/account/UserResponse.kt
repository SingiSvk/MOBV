package com.example.mobv.data.api.user.account

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

data class UserResponse (
    val uid: String,
    val access: String,
    val refresh: String
){
}