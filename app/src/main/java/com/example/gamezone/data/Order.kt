package com.example.gamezone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String,
    val userId: String,
    val totalAmount: Double,
    val status: String = "pending",
    val shippingAddress: String,
    val paymentMethod: String,
    val createdAt: Long = System.currentTimeMillis()
)
