package com.example.mobv.data.db.model

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PubDao {

    @Query("SELECT * from pubs ORDER BY name ASC")
    fun getItems(): LiveData<List<Pub>?>

    @Query("SELECT * from pubs WHERE id = :id")
    fun getPub(id: Long): Flow<Pub>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pub: Pub)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pubs: List<Pub>)

    @Update
    suspend fun update(pub: Pub)

    @Delete
    suspend fun delete(pub: Pub)

    @Query("DELETE FROM pubs")
    suspend fun deleteAll()
}