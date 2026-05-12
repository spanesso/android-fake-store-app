package com.mango.fakestore.core.database

import androidx.room.RoomDatabase

// `:core:database` define la clase base abstracta sin la anotación @Database porque
// Room requiere al menos una entidad en el array `entities`. El ensamblaje ocurre
// en `:app`, que declara la clase concreta con @Database(entities=[ProductEntity::class, ...]).
abstract class MangoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "mango_store.db"
        const val DATABASE_VERSION = 1
    }
}
