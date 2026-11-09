package com.example.materna.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MaternaDatabaseProvider {
    @Volatile
    private var INSTANCE: MaternaDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE app_settings ADD COLUMN emailPendente TEXT"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE app_settings ADD COLUMN idDoadora INTEGER"
            )
        }
    }

    fun getDatabase(context: Context): MaternaDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                MaternaDatabase::class.java,
                "materna_database"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                )
                .build()
                .also { database ->
                    INSTANCE = database
                }
        }
    }
}