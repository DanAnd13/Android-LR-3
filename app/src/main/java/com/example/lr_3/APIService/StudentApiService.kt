package com.example.lr_3


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentApiService {

    @GET("students/{id}")
    fun getStudent(@Path("id") id: Int): Call<StudentResponse>
}