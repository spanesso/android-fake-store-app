package com.example.fakestoreapp.database

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mango.fakestore.core.database.MangoDatabase
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.products.data.local.ProductosDao
import com.mango.fakestore.features.products.data.local.entity.ProductoEntity
import com.mango.fakestore.features.profile.data.local.PerfilDao
import com.mango.fakestore.features.profile.data.local.entity.PerfilEntity

@Database(
    entities = [ProductoEntity::class, FavoritoEntity::class, PerfilEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : MangoDatabase() {
    abstract fun productosDao(): ProductosDao
    abstract fun favoritosDao(): FavoritosDao
    abstract fun perfilDao(): PerfilDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `perfiles` (
                        `id` INTEGER NOT NULL PRIMARY KEY,
                        `nombreCompleto` TEXT NOT NULL,
                        `nombreUsuario` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `telefono` TEXT NOT NULL,
                        `ciudad` TEXT NOT NULL,
                        `calle` TEXT NOT NULL,
                        `numeroCalle` INTEGER NOT NULL,
                        `codigoPostal` TEXT NOT NULL,
                        `cachadoEn` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE IF EXISTS `favoritos`")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `favoritos` (
                        `productoId` INTEGER NOT NULL,
                        `userId` INTEGER NOT NULL DEFAULT 0,
                        `titulo` TEXT NOT NULL,
                        `precio` REAL NOT NULL,
                        `imagenUrl` TEXT NOT NULL,
                        `categoria` TEXT NOT NULL,
                        `fechaMarcado` INTEGER NOT NULL,
                        PRIMARY KEY(`productoId`, `userId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
