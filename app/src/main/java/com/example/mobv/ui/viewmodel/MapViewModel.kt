package com.example.mobv.ui.viewmodel

import androidx.lifecycle.*
import com.example.mobv.data.DataService
import com.example.mobv.helpers.Evento
import com.example.mobv.ui.viewmodel.data.MyLocation
import com.example.mobv.ui.viewmodel.data.NearbyPub
import kotlinx.coroutines.launch

class MapViewModel(private val repository: DataService): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)
    val myPub = MutableLiveData<NearbyPub>(null)

    private val _checkedIn = MutableLiveData<Evento<Boolean>>()
    val checkedIn: LiveData<Evento<Boolean>>
        get() = _checkedIn

    val pubs : LiveData<List<NearbyPub>> = myLocation.switchMap {
        liveData {
            loading.postValue(true)
            it?.let { it ->
                val b = repository.nearbyPubs(it.lat,it.lon) { _message.postValue(Evento(it)) }
                emit(b)
                if (myPub.value==null){
                    myPub.postValue(b.firstOrNull())
                }
            } ?: emit(listOf())
            loading.postValue(false)
        }
    }

    fun checkIn(){
        viewModelScope.launch {
            loading.postValue(true)
            myPub.value?.let { it ->
                repository.pubCheckIn(
                    it,
                    {_message.postValue(Evento(it))},
                    {_checkedIn.postValue(Evento(it))})
            }
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}