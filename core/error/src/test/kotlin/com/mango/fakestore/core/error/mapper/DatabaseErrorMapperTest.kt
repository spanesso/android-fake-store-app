package com.mango.fakestore.core.error.mapper

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.mango.fakestore.core.error.DomainError
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseErrorMapperTest {

    private val mapper = DatabaseErrorMapper()

    @Test
    fun `SQLiteConstraintException se mapea a IntegrityViolation`() {
        val result = mapper.map(SQLiteConstraintException("constraint"))
        assertTrue(result is DomainError.Database.IntegrityViolation)
    }

    @Test
    fun `SQLiteException se mapea a WriteFailed`() {
        val result = mapper.map(SQLiteException("any sqlite error"))
        assertTrue(result is DomainError.Database.WriteFailed)
    }

    @Test
    fun `Throwable generico se mapea a ReadFailed`() {
        val result = mapper.map(RuntimeException("unknown db error"))
        assertTrue(result is DomainError.Database.ReadFailed)
    }
}
