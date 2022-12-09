package com.example.mobv.ui.widget.detailList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.helpers.autoNotify
import kotlin.properties.Delegates

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.PubDetailItemViewHolder>() {
    var items: List<PubDetailItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.key.compareTo(n.key) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubDetailItemViewHolder {
        return PubDetailItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PubDetailItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class PubDetailItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.pub_detail_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: PubDetailItem) {
            itemView.findViewById<TextView>(R.id.name).text = item.key
            itemView.findViewById<TextView>(R.id.value).text = item.value
        }
    }
}