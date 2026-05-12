package com.mango.fakestore.core.common.ext

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import org.junit.Assert.assertEquals
import org.junit.Test

class EitherExtTest {

    @Test
    fun `fold sobre Right devuelve onRight`() {
        val result = "ok".right().fold(onLeft = { "error" }, onRight = { it.uppercase() })
        assertEquals("OK", result)
    }

    @Test
    fun `fold sobre Left devuelve onLeft`() {
        val result = "error".left().fold(onLeft = { it.uppercase() }, onRight = { "ok" })
        assertEquals("ERROR", result)
    }

    @Test
    fun `getOrElse con Right devuelve el valor`() {
        val either: Either<String, Int> = Either.Right(42)
        val result = either.getOrElse { 0 }
        assertEquals(42, result)
    }

    @Test
    fun `getOrElse con Left devuelve el default`() {
        val either: Either<String, Int> = Either.Left("err")
        val result = either.getOrElse { 0 }
        assertEquals(0, result)
    }

    @Test
    fun `flatMapRight encadena Right correctamente`() {
        val either: Either<String, Int> = Either.Right(5)
        val result: Either<String, Int> = either.flatMapRight { (it * 2).right() }
        assertEquals(Either.Right(10), result)
    }

    @Test
    fun `flatMapRight sobre Left no ejecuta la funcion`() {
        var called = false
        val either: Either<String, Int> = Either.Left("err")
        val result: Either<String, Int> = either.flatMapRight {
            called = true
            0.right()
        }
        assertEquals(false, called)
        assertEquals(Either.Left("err"), result)
    }
}
