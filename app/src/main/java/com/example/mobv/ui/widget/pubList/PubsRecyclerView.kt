package com.example.mobv.ui.widget.pubList

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.data.db.model.Pub
import com.example.mobv.ui.fragment.PubsFragmentDirections

class PubsRecyclerView : RecyclerView {
    private lateinit var pubsAdapter: PubsAdapter

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        pubsAdapter = PubsAdapter(object : PubsEvents {
            override fun onPubClick(pub: Pub) {
                this@PubsRecyclerView.findNavController().navigate(
                    PubsFragmentDirections.actionToPubDetails(pub.id, pub.users)
                )
            }
        })
        adapter = pubsAdapter
    }
}

@BindingAdapter(value = ["pub_items"])
fun PubsRecyclerView.applyItems(
    pubs: List<Pub>?
) {
    (adapter as PubsAdapter).items = pubs ?: emptyList()
}