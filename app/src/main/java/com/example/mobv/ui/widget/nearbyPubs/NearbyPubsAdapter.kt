package com.example.mobv.ui.widget.nearbyPubs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.helpers.autoNotify
import com.example.mobv.ui.viewmodel.data.NearbyPub
import com.google.android.material.chip.Chip
import kotlin.properties.Delegates

class NearbyPubsAdapter(private val events: NearbyPubsEvents? = null) :
    RecyclerView.Adapter<NearbyPubsAdapter.NearbyPubItemViewHolder>() {
    var items: List<NearbyPub> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyPubItemViewHolder {
        return NearbyPubItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NearbyPubItemViewHolder, position: Int) {
        holder.bind(items[position], events)
    }

    class NearbyPubItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.nearby_pub_item,
            parent,
            false)
        ) : RecyclerView.ViewHolder(itemView){

        fun bind(item: NearbyPub, events: NearbyPubsEvents? = null) {
            itemView.findViewById<TextView>(R.id.name).text = item.name
            itemView.findViewById<TextView>(R.id.distance).text = "%.2f m".format(item.distance)
            itemView.findViewById<Chip>(R.id.type).text = item.type

            itemView.setOnClickListener { events?.onPubClick(item) }
        }
    }
}