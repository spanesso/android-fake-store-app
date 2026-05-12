package com.mango.fakestore.core.logging

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.logging.impl.TimberLogger
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class TimberLoggerTest {

    private val mensajesCapturados = mutableListOf<Pair<Int, String>>()

    @Before
    fun setUp() {
        Timber.uprootAll()
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                mensajesCapturados += priority to message
            }
        })
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
        mensajesCapturados.clear()
    }

    @Test
    fun `TimberLogger implementa Logger`() {
        val logger: Logger = TimberLogger()
        assertThat(logger).isInstanceOf(Logger::class.java)
    }

    @Test
    fun `info no lanza excepcion y emite log`() {
        val logger = TimberLogger()
        logger.info("TAG", "mensaje de info")
        assertThat(mensajesCapturados).isNotEmpty()
    }

    @Test
    fun `warn sin causa no lanza excepcion y emite log`() {
        val logger = TimberLogger()
        logger.warn("TAG", "advertencia sin causa")
        assertThat(mensajesCapturados).isNotEmpty()
    }

    @Test
    fun `warn con causa no lanza excepcion y emite log`() {
        val logger = TimberLogger()
        logger.warn("TAG", "advertencia con causa", RuntimeException("test"))
        assertThat(mensajesCapturados).isNotEmpty()
    }

    @Test
    fun `error sin causa no lanza excepcion y emite log`() {
        val logger = TimberLogger()
        logger.error("TAG", "error sin causa")
        assertThat(mensajesCapturados).isNotEmpty()
    }

    @Test
    fun `error con causa no lanza excepcion y emite log`() {
        val logger = TimberLogger()
        logger.error("TAG", "error con causa", IllegalArgumentException("arg invalido"))
        assertThat(mensajesCapturados).isNotEmpty()
    }

    @Test
    fun `TimberLogger no planta arbol adicional si ya existe uno`() {
        val conteoBefore = Timber.treeCount
        TimberLogger()
        assertThat(Timber.treeCount).isEqualTo(conteoBefore)
    }

    @Test
    fun `TimberLogger planta arbol cuando no hay ninguno`() {
        Timber.uprootAll()
        assertThat(Timber.treeCount).isEqualTo(0)
        TimberLogger()
        assertThat(Timber.treeCount).isGreaterThan(0)
    }
}
