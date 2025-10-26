package com.example.gamezone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String,
    val userId: String, // Email of the user
    val totalAmount: Double,
    val status: String = "pending", // pending, confirmed, shipped, delivered, cancelled
    val shippingAddress: String,
    val paymentMethod: String,
    val createdAt: Long = System.currentTimeMillis()
)
