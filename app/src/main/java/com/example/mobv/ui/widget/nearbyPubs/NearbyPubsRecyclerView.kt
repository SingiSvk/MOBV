package com.example.mobv.ui.widget.nearbyPubs

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.ui.viewmodel.data.NearbyPub

class NearbyPubsRecyclerView : RecyclerView {
    private lateinit var pubsAdapter: NearbyPubsAdapter
    var events: NearbyPubsEvents? = null

    constructor(context: Context) : super(context) {
        init(context)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        pubsAdapter = NearbyPubsAdapter(object : NearbyPubsEvents {
            override fun onPubClick(nearbyPub: NearbyPub) {
                events?.onPubClick(nearbyPub)
            }

        })
        adapter = pubsAdapter
    }
}

@BindingAdapter(value = ["nearby_pubs"])
fun NearbyPubsRecyclerView.applyNearbyPubs(
    pubs: List<NearbyPub>
) {
    (adapter as NearbyPubsAdapter).items = pubs
}