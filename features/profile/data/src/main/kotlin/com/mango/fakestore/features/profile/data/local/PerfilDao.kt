package com.mango.fakestore.features.profile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mango.fakestore.features.profile.data.local.entity.PerfilEntity

@Dao
interface PerfilDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: PerfilEntity)

    @Query("SELECT * FROM perfiles WHERE id = :id")
    suspend fun obtenerPerfil(id: Int): PerfilEntity?

    @Query("DELETE FROM perfiles WHERE id = :id")
    suspend fun borrarPerfil(id: Int)
}
