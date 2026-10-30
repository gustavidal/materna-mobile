package com.example.materna.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val onboardingCompleted: Boolean = false,
    val emailPendente: String? = null
)