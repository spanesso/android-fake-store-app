package com.mango.fakestore.core.error.mapper

import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError.Severity
import com.mango.fakestore.core.error.UiError.UiErrorAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainErrorToUiErrorMapperTest {

    private val mapper = DomainErrorToUiErrorMapper()

    @Test
    fun `Network_NoConnection se mapea con severidad Blocking y accion Retry`() {
        val result = mapper.map(DomainError.Network.NoConnection())
        assertEquals(Severity.Blocking, result.severity)
        assertTrue(result.actions.contains(UiErrorAction.Retry))
        assertEquals("NET-000", result.errorCode)
    }

    @Test
    fun `Network_Timeout se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Network.Timeout())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("NET-001", result.errorCode)
    }

    @Test
    fun `Network_Server se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Network.Server(503))
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("NET-500", result.errorCode)
    }

    @Test
    fun `Network_Unauthorized se mapea con severidad Fatal y accion Login`() {
        val result = mapper.map(DomainError.Network.Unauthorized())
        assertEquals(Severity.Fatal, result.severity)
        assertTrue(result.actions.contains(UiErrorAction.Login))
        assertEquals("NET-401", result.errorCode)
    }

    @Test
    fun `Network_Forbidden se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Network.Forbidden())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("NET-403", result.errorCode)
    }

    @Test
    fun `Network_NotFound se mapea con severidad Info`() {
        val result = mapper.map(DomainError.Network.NotFound())
        assertEquals(Severity.Info, result.severity)
        assertEquals("NET-404", result.errorCode)
    }

    @Test
    fun `Network_Parsing se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Network.Parsing())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("NET-002", result.errorCode)
    }

    @Test
    fun `Database_ReadFailed se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Database.ReadFailed())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("DB-001", result.errorCode)
    }

    @Test
    fun `Database_WriteFailed se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Database.WriteFailed())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("DB-002", result.errorCode)
    }

    @Test
    fun `Database_NotFound se mapea con severidad Info`() {
        val result = mapper.map(DomainError.Database.NotFound())
        assertEquals(Severity.Info, result.severity)
        assertEquals("DB-003", result.errorCode)
    }

    @Test
    fun `Database_IntegrityViolation se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Database.IntegrityViolation())
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("DB-004", result.errorCode)
    }

    @Test
    fun `Security_BiometricUnavailable se mapea con severidad Warning`() {
        val result = mapper.map(DomainError.Security.BiometricUnavailable)
        assertEquals(Severity.Warning, result.severity)
        assertEquals("SEC-001", result.errorCode)
    }

    @Test
    fun `Security_BiometricLockout se mapea con severidad Blocking`() {
        val result = mapper.map(DomainError.Security.BiometricLockout)
        assertEquals(Severity.Blocking, result.severity)
        assertEquals("SEC-002", result.errorCode)
    }

    @Test
    fun `Security_RootDetected se mapea con severidad Fatal`() {
        val result = mapper.map(DomainError.Security.RootDetected)
        assertEquals(Severity.Fatal, result.severity)
        assertEquals("SEC-003", result.errorCode)
    }

    @Test
    fun `Security_IntegrityFailed se mapea con severidad Fatal`() {
        val result = mapper.map(DomainError.Security.IntegrityFailed)
        assertEquals(Severity.Fatal, result.severity)
        assertEquals("SEC-004", result.errorCode)
    }

    @Test
    fun `Security_SessionExpired se mapea con accion Login`() {
        val result = mapper.map(DomainError.Security.SessionExpired)
        assertEquals(Severity.Fatal, result.severity)
        assertTrue(result.actions.contains(UiErrorAction.Login))
        assertEquals("SEC-005", result.errorCode)
    }

    @Test
    fun `Validation se mapea con severidad Warning`() {
        val result = mapper.map(DomainError.Validation(mapOf("email" to "Requerido")))
        assertEquals(Severity.Warning, result.severity)
        assertEquals("VAL-001", result.errorCode)
    }

    @Test
    fun `Unknown se mapea con severidad Blocking y acciones Retry y Dismiss`() {
        val result = mapper.map(DomainError.Unknown())
        assertEquals(Severity.Blocking, result.severity)
        assertTrue(result.actions.contains(UiErrorAction.Retry))
        assertTrue(result.actions.contains(UiErrorAction.Dismiss))
        assertEquals("UNK-000", result.errorCode)
    }
}
