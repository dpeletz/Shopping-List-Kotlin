package com.example.shoppinglistkotlin.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "price") var price: Float,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "intCategory") var intCategory: Int,
    @ColumnInfo(name = "status") var status: Boolean,
    @ColumnInfo(name = "quantity") var quantity: Int
) : Serializable