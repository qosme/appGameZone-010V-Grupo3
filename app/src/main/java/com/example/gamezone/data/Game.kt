package com.example.gamezone.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.DrawableRes

@Entity(tableName = "games")
data class Game(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val longDescription: String,
    val price: Double,
    val category: String,
    val rating: Double,
    val releaseDate: String,
    val developer: String,
    val publisher: String,
    val imageResId: Int,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
