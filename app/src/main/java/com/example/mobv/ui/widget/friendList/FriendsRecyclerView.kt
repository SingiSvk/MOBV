package com.example.mobv.ui.widget.friendList

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.data.db.model.Pub
import com.example.mobv.ui.fragment.FriendsFragmentDirections
import com.example.mobv.ui.viewmodel.data.PubFriend

class FriendsRecyclerView : RecyclerView {
    private lateinit var friendsAdapter: FriendsAdapter

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        friendsAdapter = FriendsAdapter(object : FriendsEvents {
            override fun onFriendClick(pubFriend: PubFriend, pubs: List<Pub>) {
                if(pubFriend.pub_id != null) {
                    val pub = pubs.find { it.id == pubFriend.pub_id }
                    this@FriendsRecyclerView.findNavController().navigate(
                        FriendsFragmentDirections.actionToPubDetails(pubFriend.pub_id, pub?.users ?: 0)
                    )
                }
            }
        })
        adapter = friendsAdapter
    }
}

@BindingAdapter(value = ["friend_items","pubs"])
fun FriendsRecyclerView.applyItems(
    friends: List<PubFriend>?,
    pubs: List<Pub>?
) {
    (adapter as FriendsAdapter).items = friends ?: emptyList()
    (adapter as FriendsAdapter).pubs = pubs ?: emptyList()
}