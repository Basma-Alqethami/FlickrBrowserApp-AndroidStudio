package com.example.flickrbrowser

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface API {
    @GET
    fun getData(@Url url: String): Call<Photos>
}