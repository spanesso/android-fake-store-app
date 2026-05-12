package com.mango.fakestore.core.analytics

import java.util.concurrent.ConcurrentHashMap

class ErrorRateLimiter(
    private val maxPorVentana: Int = 10,
    private val ventanaMs: Long = 60_000L,
) {
    private val registros = ConcurrentHashMap<String, ArrayDeque<Long>>()

    fun permitir(errorCode: String): Boolean {
        val ahora = System.currentTimeMillis()
        val deque = registros.getOrPut(errorCode) { ArrayDeque() }
        synchronized(deque) {
            val iter = deque.iterator()
            while (iter.hasNext()) {
                if (ahora - iter.next() > ventanaMs) iter.remove()
            }
            if (deque.size >= maxPorVentana) return false
            deque.addLast(ahora)
            return true
        }
    }
}
