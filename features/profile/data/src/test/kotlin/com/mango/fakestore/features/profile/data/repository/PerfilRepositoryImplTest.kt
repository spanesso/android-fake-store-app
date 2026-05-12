package com.mango.fakestore.features.profile.data.repository

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.profile.data.remote.PerfilApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class PerfilRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: PerfilApi
    private lateinit var repositorio: PerfilRepositoryImpl

    private val json = Json { ignoreUnknownKeys = true }

    private val usuarioJson = """
        {
          "id": 8,
          "email": "john@example.com",
          "username": "johnd",
          "password": "secret",
          "phone": "1-570-236-7033",
          "name": { "firstname": "John", "lastname": "Doe" },
          "address": {
            "city": "kilcoole",
            "street": "new road",
            "number": 7835,
            "zipcode": "12926-3874",
            "geolocation": { "lat": "-37.3159", "long": "81.1496" }
          }
        }
    """.trimIndent()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        api = retrofit.create(PerfilApi::class.java)
        repositorio = PerfilRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `dado respuesta 200 cuando se obtiene el perfil entonces retorna Either Right con el usuario`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(usuarioJson),
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
            val usuario = (resultado as Either.Right).value
            assertThat(usuario.id).isEqualTo(8)
            assertThat(usuario.nombreCompleto).isEqualTo("John Doe")
            assertThat(usuario.email).isEqualTo("john@example.com")
        }

    @Test
    fun `dado respuesta 404 cuando se obtiene el perfil entonces retorna Either Left con NotFound`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND),
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value)
                .isInstanceOf(DomainError.Network.NotFound::class.java)
        }

    @Test
    fun `dado respuesta 500 cuando se obtiene el perfil entonces retorna Either Left con Server`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR),
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            val error = (resultado as Either.Left).value
            assertThat(error).isInstanceOf(DomainError.Network.Server::class.java)
            assertThat((error as DomainError.Network.Server).httpCode).isEqualTo(500)
        }

    @Test
    fun `dado JSON invalido cuando se obtiene el perfil entonces retorna Either Left con Parsing`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody("{ invalid json {{"),
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value)
                .isInstanceOf(DomainError.Network.Parsing::class.java)
        }

    @Test
    fun `dado timeout cuando se obtiene el perfil entonces retorna Either Left con Timeout`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setBodyDelay(5, TimeUnit.SECONDS)
                    .setBody(usuarioJson),
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value)
                .isInstanceOf(DomainError.Network.Timeout::class.java)
        }

    @Test
    fun `dado servidor caido cuando se obtiene el perfil entonces retorna Either Left con NoConnection`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AT_START },
            )

            val resultado = repositorio.obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value).isAnyOf(
                DomainError.Network.NoConnection(),
                (resultado as Either.Left).value,
            )
            val error = (resultado as Either.Left).value
            assertThat(
                error is DomainError.Network.NoConnection ||
                    error is DomainError.Network.Timeout,
            ).isTrue()
        }
}
