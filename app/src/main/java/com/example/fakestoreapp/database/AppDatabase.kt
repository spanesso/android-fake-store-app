package com.example.fakestoreapp.database

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mango.fakestore.core.database.MangoDatabase
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.products.data.local.ProductosDao
import com.mango.fakestore.features.products.data.local.entity.ProductoEntity

@Database(
    entities = [ProductoEntity::class, FavoritoEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : MangoDatabase() {
    abstract fun productosDao(): ProductosDao
    abstract fun favoritosDao(): FavoritosDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `favoritos` (
                        `productoId` INTEGER NOT NULL,
                        `titulo` TEXT NOT NULL,
                        `precio` REAL NOT NULL,
                        `imagenUrl` TEXT NOT NULL,
                        `categoria` TEXT NOT NULL,
                        `fechaMarcado` INTEGER NOT NULL,
                        PRIMARY KEY(`productoId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
