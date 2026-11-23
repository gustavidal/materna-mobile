package com.example.materna.services.cloudinary

import com.example.materna.models.responses.CloudinaryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryService {
    @Multipart
    @POST("v1_1/materna-perfis/image/upload")
    fun enviarImagem(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): Call<CloudinaryUploadResponse>

    @Multipart
    @POST("v1_1/materna-perfis/auto/upload")
    fun enviarDocumento(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): Call<CloudinaryUploadResponse>
}
