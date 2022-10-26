package com.example.mobv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.fragment.PubListFragmentDirections
import com.example.mobv.model.Pub

class ItemAdapter(private val context: Context, private val dataset: List<Pub>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.pub_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.pub_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.button.text = item.tags.name

        var name = ""
        if (item.tags.name != null)
            name = item.tags.name

        holder.button.setOnClickListener{
            val action = PubListFragmentDirections.actionPubListToPubDetails(position, name, item.lat, item.lon)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = dataset.size
}