package com.example.materna.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppSettings::class,
        JornadaLocalProgress::class
    ],
    version = 6,
    exportSchema = false
)
abstract class MaternaDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun jornadaLocalProgressDao(): JornadaLocalProgressDao
}