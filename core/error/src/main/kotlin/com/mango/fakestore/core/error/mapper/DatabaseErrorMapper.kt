package com.mango.fakestore.core.error.mapper

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.mango.fakestore.core.error.DomainError

class DatabaseErrorMapper {
    fun map(throwable: Throwable): DomainError.Database = when (throwable) {
        is SQLiteConstraintException -> DomainError.Database.IntegrityViolation(throwable)
        is SQLiteException -> DomainError.Database.WriteFailed(throwable)
        else -> DomainError.Database.ReadFailed(throwable)
    }
}
