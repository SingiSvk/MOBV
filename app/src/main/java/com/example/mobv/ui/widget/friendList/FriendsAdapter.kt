package com.example.mobv.ui.widget.friendList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.data.db.model.Pub
import com.example.mobv.helpers.autoNotify
import com.example.mobv.ui.viewmodel.data.PubFriend
import kotlin.properties.Delegates

class FriendsAdapter(private val events: FriendsEvents? = null) :
    RecyclerView.Adapter<FriendsAdapter.FriendItemViewHolder>() {
    var items: List<PubFriend> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.user_id.compareTo(n.user_id) == 0 }
    }

    var pubs: List<Pub> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendItemViewHolder {
        return FriendItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FriendItemViewHolder, position: Int) {
        holder.bind(items[position], pubs, events)
    }

    class FriendItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.friend_item,
            parent,
            false)
        ) : RecyclerView.ViewHolder(itemView){

        fun bind(item: PubFriend, pubs: List<Pub>, events: FriendsEvents?) {
            itemView.findViewById<TextView>(R.id.name).text = item.user_name
            itemView.findViewById<TextView>(R.id.pub).text = item.pub_name

            itemView.setOnClickListener { events?.onFriendClick(item, pubs) }
        }
    }
}