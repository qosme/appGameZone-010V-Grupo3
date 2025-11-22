package com.example.gamezone.data

import retrofit2.http.GET

interface RestDataSource {
    @GET("?inc=email,login,name,phone")
    suspend fun getUsers(): ApiResponse
}