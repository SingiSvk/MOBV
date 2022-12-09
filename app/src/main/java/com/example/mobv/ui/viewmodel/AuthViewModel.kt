package com.example.mobv.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.DataService
import com.example.mobv.data.api.user.account.UserResponse
import com.example.mobv.helpers.Evento
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

class AuthViewModel(private val repository: DataService): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val user = MutableLiveData<UserResponse>(null)

    val loading = MutableLiveData(false)

    fun login(name: String, password: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.login(
                name,hashPassword(password),
                { _message.postValue(Evento(it)) },
                { user.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    fun signup(name: String, password: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.createUser(
                name,hashPassword(password),
                { _message.postValue(Evento(it)) },
                { user.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    private fun hashPassword(password: String): String {
        val salt = "definitelyrandomsalt"

        var generatedPassword: String? = null
        try {
            val md = MessageDigest.getInstance("SHA-512")
            md.update(salt.toByteArray())
            val bytes = md.digest(password.toByteArray())
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(
                    ((bytes[i] and 0xff.toByte()) + 0x100).toString(16)
                        .substring(1)
                )
            }
            generatedPassword = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return generatedPassword?:password
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}