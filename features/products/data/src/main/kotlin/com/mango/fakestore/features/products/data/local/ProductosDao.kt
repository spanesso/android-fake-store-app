package com.mango.fakestore.features.products.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mango.fakestore.features.products.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductosDao {
    @Query("SELECT * FROM productos ORDER BY id ASC")
    fun observarProductos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM productos")
    suspend fun borrarTodos()
}
