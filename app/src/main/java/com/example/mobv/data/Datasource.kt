package com.example.mobv.data

import com.example.mobv.model.Pubs
import com.google.gson.Gson

class Datasource {


    fun loadPubs(json: String): Pubs {
        return Gson().fromJson(json, Pubs::class.java);
    }
}