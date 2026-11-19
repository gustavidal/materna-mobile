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

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE app_settings ADD COLUMN idDoadoraPendente INTEGER"
            )
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE app_settings ADD COLUMN emailDoadora TEXT"
            )
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS jornada_local_progress (
                    idDoadora INTEGER NOT NULL,
                    idJornada INTEGER NOT NULL,
                    idEtapa TEXT NOT NULL,
                    concluida INTEGER NOT NULL,
                    dataConclusao INTEGER NOT NULL,
                    PRIMARY KEY (
                        idDoadora,
                        idJornada,
                        idEtapa
                    )
                )
                """.trimIndent()
            )
        }
    }

    fun getDatabase(context: Context): MaternaDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room
                .databaseBuilder(
                    context.applicationContext,
                    MaternaDatabase::class.java,
                    "materna_database"
                )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6
                )
                .build()
                .also { database ->
                    INSTANCE = database
                }
        }
    }
}