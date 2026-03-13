package com.johannjara.docvault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.johannjara.docvault.data.local.dao.DocumentDao
import com.johannjara.docvault.data.local.entity.DocumentEntity

@Database(entities = [DocumentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
