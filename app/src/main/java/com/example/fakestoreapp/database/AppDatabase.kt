package com.example.fakestoreapp.database

import androidx.room.Database
import com.mango.fakestore.core.database.MangoDatabase
import com.mango.fakestore.features.products.data.local.ProductosDao
import com.mango.fakestore.features.products.data.local.entity.ProductoEntity

@Database(
    entities = [ProductoEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : MangoDatabase() {
    abstract fun productosDao(): ProductosDao
}
