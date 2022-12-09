package com.example.mobv.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mobv.data.api.pubs.PubsApiInterface
import com.example.mobv.data.api.user.UserApiInterface
import com.example.mobv.data.api.user.account.LoginRequest
import com.example.mobv.data.api.user.account.RegisterRequest
import com.example.mobv.data.api.user.account.UserResponse
import com.example.mobv.data.api.user.friends.AddFriendRequest
import com.example.mobv.data.api.user.pubs.PubRequest
import com.example.mobv.data.api.user.pubs.RemoveFromPubRequest
import com.example.mobv.data.db.PubLocalCache
import com.example.mobv.data.db.model.Friend
import com.example.mobv.data.db.model.Pub
import com.example.mobv.ui.viewmodel.data.MyLocation
import com.example.mobv.ui.viewmodel.data.NearbyPub
import com.example.mobv.ui.viewmodel.data.PubFriend
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DataService private constructor(
    private val pubsService: PubsApiInterface,
    private val userService: UserApiInterface,
    private val cache: PubLocalCache
){

    suspend fun createUser(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        try {
            val resp = userService.register(RegisterRequest(name = name, password = password))
            if (resp.isSuccessful) {
                resp.body()?.let { user ->
                    if (user.uid == "-1"){
                        onStatus(null)
                        onError("Name already exists. Choose another.")
                    }else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Failed to sign up, try again later.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Sign up failed, check internet connection")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Sign up failed, error.")
            onStatus(null)
        }
    }

    suspend fun login(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        try {
            val resp = userService.login(LoginRequest(name = name, password = password))
            if (resp.isSuccessful) {
                Log.e("name", name)
                Log.e("pas", password)
                Log.e("login", resp.body().toString())
                resp.body()?.let { user ->
                    if (user.uid == "-1"){
                        onStatus(null)
                        onError("Wrong name or password.")
                    }else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Failed to login, try again later.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Login failed, check internet connection")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Login failed, error.")
            onStatus(null)
        }
    }

    suspend fun pubList(
        onError: (error: String) -> Unit
    ) {
        try {
            val resp = userService.getPubs()
            if (resp.isSuccessful) {
                resp.body()?.let { pubs ->
                    val b = pubs.map {
                        Pub(
                            it.bar_id,
                            it.bar_name,
                            it.bar_type,
                            it.lat,
                            it.lon,
                            it.users
                        )
                    }

                    cache.deleteAll()
                    cache.insertAll(b)
                } ?: onError("Failed to load pubs")
            } else {
                onError("Failed to read pubs")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load pubs, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load pubs, error.")
        }
    }

    suspend fun nearbyPubs(
        lat: Double, lon: Double,
        onError: (error: String) -> Unit
    ) : List<NearbyPub> {
        var nearby = listOf<NearbyPub>()
        try {
            val q = "[out:json];node(around:250,$lat,$lon);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
            val resp = pubsService.getPubsAround(q)
            if (resp.isSuccessful) {
                resp.body()?.let { pubs ->
                    nearby = pubs.elements.map {
                        NearbyPub(it.id,it.tags.getOrDefault("name",""), it.tags.getOrDefault("amenity",""),it.lat,it.lon,it.tags).apply {
                            distance = distanceTo(MyLocation(lat,lon))
                        }
                    }
                    nearby = nearby.filter { it.name.isNotBlank() }.sortedBy { it.distance }
                } ?: onError("Failed to load pubs")
            } else {
                onError("Failed to read pubs")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load pubs, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load pubs, error.")
        }
        return nearby
    }

    suspend fun pubDetail(
        id: String,
        onError: (error: String) -> Unit
    ) : NearbyPub? {
        var nearby:NearbyPub? = null
        try {
            val q = "[out:json];node($id);out body;>;out skel;"
            val resp = pubsService.getPubs(q)
            if (resp.isSuccessful) {
                resp.body()?.let { pubs ->
                    if (pubs.elements.isNotEmpty()) {
                        val b = pubs.elements[0]
                        nearby = NearbyPub(
                            b.id,
                            b.tags.getOrDefault("name", ""),
                            b.tags.getOrDefault("amenity", ""),
                            b.lat,
                            b.lon,
                            b.tags,
                        )
                    }
                } ?: onError("Failed to load pubs")
            } else {
                onError("Failed to read pubs")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load pubs, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load pubs, error.")
        }
        return nearby
    }

    suspend fun pubCheckIn(
        pub: NearbyPub,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp = userService.setInPub(PubRequest(pub.id,pub.name,pub.type,pub.lat,pub.lon))
            if (resp.isSuccessful) {
                resp.body()?.let {
                    onSuccess(true)
                }
            } else {
                onError("Failed to Check in, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Check in failed, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Check in failed, error.")
        }
    }

    suspend fun pubCheckOut(
        pub: NearbyPub,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {

        try {
            val resp = userService.removeFromPub(RemoveFromPubRequest("",pub.name,pub.type,pub.lat,pub.lon))
            if (resp.isSuccessful) {
                resp.body()?.let {
                    onSuccess(true)
                }
            } else {
                onError("Failed to check out, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Check out failed, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Check out failed, error.")
        }
    }

    suspend fun listFriends(
        onError: (error: String) -> Unit
    ) : List<PubFriend> {
        val friendsList = ArrayList<PubFriend>()
        try {
            val resp = userService.listFriends()
            if (resp.isSuccessful) {
                resp.body()?.let { friends ->
                    friendsList.addAll(friends.map {
                        PubFriend(
                            it.user_id,
                            it.user_name,
                            it.bar_id,
                            it.bar_name,
                            it.bar_lat,
                            it.bar_lon,
                        )
                    })
                } ?: onError("Failed to load friends")
            } else {
                onError("Failed to load friends")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load friends, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load friends, error.")
        }
        return friendsList
    }

    suspend fun getMyFriends(
        onError: (error: String) -> Unit
    ) : List<Friend> {
        val friendsList = ArrayList<Friend>()
        try {
            val resp = userService.getMyFriends()
            if (resp.isSuccessful) {
                resp.body()?.let { friends ->
                    friendsList.addAll(friends.map {
                        Friend(
                            it.user_id,
                            it.user_name,
                        )
                    })

                    /*
                    cache.deleteAll()
                    cache.insertAll(b)

                     */
                } ?: onError("Failed to load friends")
            } else {
                onError("Failed to load friends")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load friends, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load friends, error.")
        }
        return friendsList
    }

    suspend fun addFriend(
        username: String,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp = userService.addFriend(AddFriendRequest(contact = username))
            if (resp.isSuccessful) {
                resp.body()?.let {
                    onSuccess(true)
                }
            } else {
                onError("Failed to check out, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Adding friend $username failed, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Adding friend $username, error.")
        }
    }

    fun dbPubs() : LiveData<List<Pub>?> {
        return cache.getItems()
    }

    companion object{
        @Volatile
        private var INSTANCE: DataService? = null

        fun getInstance(pubsService: PubsApiInterface, userService: UserApiInterface, cache: PubLocalCache): DataService =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: DataService(pubsService, userService, cache).also { INSTANCE = it }
            }

        @SuppressLint("SimpleDateFormat")
        fun dateToTimeStamp(date: String): Long {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)?.time ?: 0L
        }

        @SuppressLint("SimpleDateFormat")
        fun timestampToDate(time: Long): String{
            val netDate = Date(time*1000)
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(netDate)
        }
    }
}