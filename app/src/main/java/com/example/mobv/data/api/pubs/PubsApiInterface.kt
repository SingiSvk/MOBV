package com.example.mobv.data.api.pubs

import android.content.Context
import com.example.mobv.data.api.helper.AuthInterceptor
import com.example.mobv.data.api.helper.TokenAuthenticator
import com.example.mobv.data.api.pubs.data.PubsResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PubsApiInterface {
    @GET("interpreter?")
    suspend fun getPubs(@Query("data") data: String): Response<PubsResponse>
    @GET("interpreter?")
    suspend fun getPubsAround(@Query("data") data: String): Response<PubsResponse>

    companion object{
        private const val BASE_URL = "https://overpass-api.de/api/"

        fun create(context: Context): PubsApiInterface {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(PubsApiInterface::class.java)
        }
    }
}