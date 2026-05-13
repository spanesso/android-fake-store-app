package com.mango.fakestore.features.favorites.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritosDao {
    @Query("SELECT * FROM favoritos WHERE userId = :userId ORDER BY fechaMarcado DESC")
    fun observarFavoritos(userId: Int): Flow<List<FavoritoEntity>>

    @Query("SELECT COUNT(*) FROM favoritos WHERE userId = :userId")
    fun observarConteo(userId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFavorito(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE productoId = :productoId AND userId = :userId")
    suspend fun borrarFavorito(productoId: Int, userId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE productoId = :productoId AND userId = :userId)")
    suspend fun esFavorito(productoId: Int, userId: Int): Boolean
}
