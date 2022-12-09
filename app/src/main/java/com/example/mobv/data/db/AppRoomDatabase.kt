package com.example.mobv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobv.data.db.model.Pub
import com.example.mobv.data.db.model.PubDao

@Database(entities = [Pub::class], version = 2, exportSchema = false)
abstract class AppRoomDatabase: RoomDatabase() {
    abstract fun appDao(): PubDao

    companion object{
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {  INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppRoomDatabase::class.java, "pubsDatabase"
            ).fallbackToDestructiveMigration()
                .build()
    }
}