package com.example.mobv.helpers

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mobv.data.DataService
import com.example.mobv.data.api.pubs.PubsApiInterface
import com.example.mobv.data.api.user.UserApiInterface
import com.example.mobv.data.db.AppRoomDatabase
import com.example.mobv.data.db.PubLocalCache

object Injection {
    private fun providePubsCache(context: Context): PubLocalCache {
        val database = AppRoomDatabase.getInstance(context)
        return PubLocalCache(database.appDao())
    }

    private fun providePubsDataRepository(context: Context): DataService {
        return DataService.getInstance(PubsApiInterface.create(context), UserApiInterface.create(context), providePubsCache(context))
    }

    fun providePubsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            providePubsDataRepository(
                context
            )
        )
    }
    /*
    private fun provideCache(context: Context): PubLocalCache {
        val database = AppRoomDatabase.getInstance(context)
        return PubLocalCache(database.appDao())
    }

    fun provideDataRepository(context: Context): DataService {
        return DataService.getInstance(RestApi.create(context), provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }*/
}