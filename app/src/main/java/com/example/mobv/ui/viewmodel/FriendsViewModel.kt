package com.example.mobv.ui.viewmodel

import androidx.lifecycle.*
import com.example.mobv.data.DataService
import com.example.mobv.data.db.model.Pub
import com.example.mobv.helpers.Evento
import com.example.mobv.ui.viewmodel.data.PubFriend
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: DataService): ViewModel() {

    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    private val _addedFriend = MutableLiveData<Evento<Boolean>>()
    val addedFriend: LiveData<Evento<Boolean>>
        get() = _addedFriend

    var friends: LiveData<List<PubFriend>?> =
        liveData {
            emit(repository.listFriends { _message.postValue(Evento(it)) })
        }

    val pubs: List<Pub>? = repository.dbPubs().value

    fun addFriend(username: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.addFriend(
                username,
                { _message.postValue(Evento(it)) },
                { _addedFriend.postValue(Evento(it)) }
            )
            loading.postValue(false)
        }
    }

    fun refreshData(){
        viewModelScope.launch {
            loading.postValue(true)
            repository.listFriends { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}

}