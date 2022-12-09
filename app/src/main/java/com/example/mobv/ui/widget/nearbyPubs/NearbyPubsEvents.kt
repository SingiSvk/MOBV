package com.example.mobv.ui.widget.nearbyPubs

import com.example.mobv.ui.viewmodel.data.NearbyPub


interface NearbyPubsEvents {
    fun onPubClick(nearbyPub: NearbyPub)
}