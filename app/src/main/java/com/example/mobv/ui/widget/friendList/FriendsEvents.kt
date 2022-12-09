package com.example.mobv.ui.widget.friendList

import com.example.mobv.data.db.model.Pub
import com.example.mobv.ui.viewmodel.data.PubFriend

interface FriendsEvents {
    fun onFriendClick(pubFriend: PubFriend, pubs: List<Pub>)
}