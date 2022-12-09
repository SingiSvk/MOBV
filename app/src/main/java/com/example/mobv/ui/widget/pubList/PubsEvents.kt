package com.example.mobv.ui.widget.pubList

import com.example.mobv.data.db.model.Pub

interface PubsEvents {
    fun onPubClick(pub: Pub)
}