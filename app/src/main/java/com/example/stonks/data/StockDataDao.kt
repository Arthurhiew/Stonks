package com.example.stonks.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repo: StockData?)

    @Delete
    fun delete(repo: StockData?)

    @Query("SELECT * FROM StockDataRepo")
    fun getAllRepos(): LiveData<List<StockData?>?>?
}
