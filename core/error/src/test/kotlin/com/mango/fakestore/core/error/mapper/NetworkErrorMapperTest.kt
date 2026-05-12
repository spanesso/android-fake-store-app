package com.mango.fakestore.core.error.mapper

import com.mango.fakestore.core.error.DomainError
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class NetworkErrorMapperTest {

    private val mapper = NetworkErrorMapper()

    @Test
    fun `UnknownHostException se mapea a NoConnection`() {
        val result = mapper.map(UnknownHostException("host"))
        assertTrue(result is DomainError.Network.NoConnection)
    }

    @Test
    fun `IOException se mapea a NoConnection`() {
        val result = mapper.map(IOException("io"))
        assertTrue(result is DomainError.Network.NoConnection)
    }

    @Test
    fun `SocketTimeoutException se mapea a Timeout`() {
        val result = mapper.map(SocketTimeoutException("timeout"))
        assertTrue(result is DomainError.Network.Timeout)
    }

    @Test
    fun `TimeoutException se mapea a Timeout`() {
        val result = mapper.map(TimeoutException("timeout"))
        assertTrue(result is DomainError.Network.Timeout)
    }

    @Test
    fun `SerializationException se mapea a Parsing`() {
        val result = mapper.map(SerializationException("parse error"))
        assertTrue(result is DomainError.Network.Parsing)
    }

    @Test
    fun `RuntimeException con HTTP 401 se mapea a Unauthorized`() {
        val result = mapper.map(RuntimeException("HTTP 401 Unauthorized"))
        assertTrue(result is DomainError.Network.Unauthorized)
    }

    @Test
    fun `RuntimeException con HTTP 500 se mapea a Server`() {
        val result = mapper.map(RuntimeException("HTTP 500 Internal Server Error"))
        val server = result as DomainError.Network.Server
        assertTrue(server.httpCode == 500)
    }
}
