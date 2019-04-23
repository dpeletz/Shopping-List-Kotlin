package com.example.shoppinglistkotlin.data

import android.arch.persistence.room.*

@Dao
interface ItemDAO {
    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>

    @Query("DELETE FROM item")
    fun deleteAll()

    @Insert
    fun insertItem(item: Item): Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)
}