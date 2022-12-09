package com.example.mobv.ui.viewmodel

import androidx.lifecycle.*
import com.example.mobv.data.DataService
import com.example.mobv.data.db.model.Pub
import com.example.mobv.helpers.Evento
import com.example.mobv.ui.viewmodel.data.MyLocation
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class PubsViewModel(private val repository: DataService): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)

    var pubs: MutableLiveData<List<Pub>?> =
        liveData {
            loading.postValue(true)
            repository.pubList { _message.postValue(Evento(it)) }
            loading.postValue(false)
            emitSource(repository.dbPubs())
        } as MutableLiveData<List<Pub>?>

    fun refreshData(){
        viewModelScope.launch {
            loading.postValue(true)
            repository.pubList { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun sortPubs(s: String){
        viewModelScope.launch {
            loading.postValue(true)
            pubs.value =
                when(s) {
                    "name" -> pubs.value?.sortedBy {it.name}
                    "users" -> pubs.value?.sortedBy {it.users}?.reversed()
                    "dist" -> pubs.value?.sortedBy {it.dist}
                    "type" -> pubs.value?.sortedBy {it.type}
                    else -> pubs.value?.sortedBy {it.name}
                }
            loading.postValue(false)
        }
    }


    fun calculateDist(){
        if (myLocation.value != null)
            pubs.value?.forEach{ pub -> pub.dist = sqrt((pub.lat - (myLocation.value!!.lat)).pow(2) + (pub.lon - myLocation.value!!.lon).pow(2)) }

    }

    fun show(msg: String){ _message.postValue(Evento(msg))}

}