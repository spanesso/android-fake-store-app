package com.mango.fakestore.core.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schema_version")
internal data class SchemaVersionEntity(@PrimaryKey val version: Int = 1)

@Database(entities = [SchemaVersionEntity::class], version = 1, exportSchema = false)
abstract class TestMangoDatabase : MangoDatabase()
