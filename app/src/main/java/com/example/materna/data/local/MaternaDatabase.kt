package com.example.materna.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppSettings::class],
    version = 2,
    exportSchema = false
)
abstract class MaternaDatabase : RoomDatabase() {

    abstract fun appSettingsDao(): AppSettingsDao
}