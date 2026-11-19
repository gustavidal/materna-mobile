package com.example.materna.services.cloudinary

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CloudinaryRetrofitFactory {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cloudinary.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getCloudinaryService(): CloudinaryService {
        return retrofit.create(CloudinaryService::class.java)
    }
}
