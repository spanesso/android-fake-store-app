package com.mango.fakestore.core.logging

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.logging.impl.NoOpLogger
import org.junit.Test

class NoOpLoggerTest {

    private val logger = NoOpLogger()

    @Test
    fun `info no lanza excepcion`() {
        logger.info("TAG", "mensaje de info")
    }

    @Test
    fun `warn sin causa no lanza excepcion`() {
        logger.warn("TAG", "advertencia sin causa")
    }

    @Test
    fun `warn con causa no lanza excepcion`() {
        logger.warn("TAG", "advertencia con causa", RuntimeException("causa"))
    }

    @Test
    fun `error sin causa no lanza excepcion`() {
        logger.error("TAG", "error sin causa")
    }

    @Test
    fun `error con causa no lanza excepcion`() {
        logger.error("TAG", "error con causa", IllegalStateException("estado invalido"))
    }

    @Test
    fun `NoOpLogger implementa Logger`() {
        val logger: Logger = NoOpLogger()
        assertThat(logger).isInstanceOf(Logger::class.java)
    }
}
