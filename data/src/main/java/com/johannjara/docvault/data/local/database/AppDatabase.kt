package com.johannjara.docvault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.johannjara.docvault.data.local.converter.Converters
import com.johannjara.docvault.data.local.dao.DocumentDao
import com.johannjara.docvault.data.local.entity.DocumentEntity

@Database(entities = [DocumentEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE documents ADD COLUMN locationName TEXT")
                db.execSQL("ALTER TABLE documents ADD COLUMN accessLogs TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}
