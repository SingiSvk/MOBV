package com.example.mobv.data.api.user

import android.content.Context
import com.example.mobv.data.api.helper.AuthInterceptor
import com.example.mobv.data.api.helper.TokenAuthenticator
import com.example.mobv.data.api.user.account.*
import com.example.mobv.data.api.user.friends.AddFriendRequest
import com.example.mobv.data.api.user.friends.FriendResponse
import com.example.mobv.data.api.user.friends.GetFriendResponse
import com.example.mobv.data.api.user.friends.RemoveFriendRequest
import com.example.mobv.data.api.user.pubs.GetPubsResponse
import com.example.mobv.data.api.user.pubs.PubRequest
import com.example.mobv.data.api.user.pubs.RemoveFromPubRequest
import com.example.mobv.data.api.user.pubs.RemoveFromPubResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApiInterface {
    @POST("user/create.php")
    suspend fun register(@Body dataModal: RegisterRequest): Response<UserResponse>

    @POST("user/login.php")
    suspend fun login(@Body dataModal: LoginRequest): Response<UserResponse>

    @POST("user/refresh.php")
    fun refreshToken(@Body dataModal: RefreshTokenRequest): Call<UserResponse>

    @Headers("mobv-auth: accept")
    @GET("bar/list.php")
    suspend fun getPubs(): Response<List<GetPubsResponse>>

    @Headers("mobv-auth: accept")
    @POST("bar/message.php")
    suspend fun setInPub(@Body dataModal: PubRequest): Response<Any>

    @Headers("mobv-auth: accept")
    @POST("bar/message.php")
    suspend fun removeFromPub(@Body dataModal: RemoveFromPubRequest): Response<RemoveFromPubResponse>

    @Headers("mobv-auth: accept")
    @POST("contact/message.php")
    suspend fun addFriend(@Body dataModal: AddFriendRequest): Response<Void>

    @Headers("mobv-auth: accept")
    @GET("contact/list.php")
    suspend fun listFriends(): Response<List<GetFriendResponse>>

    @Headers("mobv-auth: accept")
    @GET("contact/friends.php")
    suspend fun getMyFriends(): Response<List<FriendResponse>>

    @Headers("mobv-auth: accept")
    @POST("contact/delete.php")
    suspend fun removeFriend(@Body dataModal: RemoveFriendRequest): Response<Any>


    companion object{
        private const val BASE_URL = "https://zadanie.mpage.sk/"

        fun create(context: Context): UserApiInterface {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UserApiInterface::class.java)
        }
    }
}