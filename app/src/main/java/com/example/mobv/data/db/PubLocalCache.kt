package com.example.mobv.data.db

import androidx.lifecycle.LiveData
import com.example.mobv.data.db.model.Pub
import com.example.mobv.data.db.model.PubDao

class PubLocalCache(private val dao: PubDao) {

    fun getItems(): LiveData<List<Pub>?> = dao.getItems()

    fun getPub(id: Long){
        dao.getPub(id)
    }

    suspend fun insert(pub: Pub){
        dao.insert(pub)
    }

    suspend fun insertAll(pubs: List<Pub>){
        dao.insertAll(pubs)
    }

    suspend fun update(pub: Pub){
        dao.update(pub)
    }

    suspend fun delete(pub: Pub){
        dao.delete(pub)
    }

    suspend fun deleteAll(){
        dao.deleteAll()
    }
}