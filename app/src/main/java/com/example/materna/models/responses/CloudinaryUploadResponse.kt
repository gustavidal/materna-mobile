package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CloudinaryUploadResponse(
    @SerializedName("public_id")
    val publicId: String,

    @SerializedName("secure_url")
    val secureUrl: String
)
