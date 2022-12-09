package com.example.mobv.ui.viewmodel

import androidx.lifecycle.*
import com.example.mobv.data.DataService
import com.example.mobv.helpers.Evento
import com.example.mobv.ui.viewmodel.data.NearbyPub
import com.example.mobv.ui.widget.detailList.PubDetailItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DataService) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val pub = MutableLiveData<NearbyPub>(null)
    val type = pub.map { it?.tags?.getOrDefault("amenity", "") ?: "" }
    val details: LiveData<List<PubDetailItem>> = pub.switchMap {
        liveData {
            it?.let { it ->
                emit(it.tags.map {
                    PubDetailItem(it.key, it.value)
                })
            } ?: emit(emptyList())
        }
    }

    fun loadPub(id: String) {
        if (id.isBlank())
            return
        viewModelScope.launch {
            loading.postValue(true)
            pub.postValue(repository.pubDetail(id) { _message.postValue(Evento(it)) })
            loading.postValue(false)
        }
    }
}