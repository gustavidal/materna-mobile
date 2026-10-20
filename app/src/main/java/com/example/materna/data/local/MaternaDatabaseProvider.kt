package com.example.materna.data.local

import android.content.Context
import androidx.room.Room

object MaternaDatabaseProvider {

    @Volatile
    private var INSTANCE: MaternaDatabase? = null

    fun getDatabase(context: Context): MaternaDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                MaternaDatabase::class.java,
                "materna_database"
            ).build().also { database ->
                INSTANCE = database
            }
        }
    }
}