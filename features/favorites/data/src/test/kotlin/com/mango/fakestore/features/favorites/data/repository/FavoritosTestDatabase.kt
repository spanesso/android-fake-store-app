package com.mango.fakestore.features.favorites.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity

@Database(entities = [FavoritoEntity::class], version = 1, exportSchema = false)
abstract class FavoritosTestDatabase : RoomDatabase() {
    abstract fun favoritosDao(): FavoritosDao
}
