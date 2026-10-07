package com.nuvio.bridge

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class LocalServer(
    private val port: Int,
    private val onStatus: (String) -> Unit
) {
    @Volatile
    private var running = false

    private var serverSocket: ServerSocket? = null
    private var acceptThread: Thread? = null

    fun start() {
        if (running) return

        running = true

        acceptThread = thread(
            name = "NuvioBridgeServer",
            start = true
        ) {
            try {
                serverSocket = ServerSocket(port)
                onStatus("Running on 127.0.0.1:$port")

                while (running) {
                    val socket = serverSocket?.accept() ?: break
                    thread(name = "NuvioRequest") {
                        handle(socket)
                    }
                }
            } catch (e: Exception) {
                if (running) {
                    onStatus("Server error: ${e.javaClass.simpleName}: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        running = false
        try {
            serverSocket?.close()
        } catch (_: Exception) {
        }
        acceptThread?.interrupt()
    }

    private fun handle(socket: Socket) {
        socket.use { s ->
            try {
                val input = BufferedInputStream(s.getInputStream())
                val output = BufferedOutputStream(s.getOutputStream())

                val request = readRequest(input)
                val firstLine = request.lineSequence().firstOrNull().orEmpty()
                val path = firstLine.split(" ").getOrNull(1)?.substringBefore("?") ?: "/"

                val responseBody = when (path) {
                    "/ping" -> """{"ok":true,"service":"nuvio-bridge"}"""
                    "/js-test" -> NuvioJs.test()
                    else -> """{"ok":false,"error":"not_found"}"""
                }

                val status = if (path == "/ping" || path == "/js-test") {
                    "200 OK"
                } else {
                    "404 Not Found"
                }

                val bodyBytes = responseBody.toByteArray(StandardCharsets.UTF_8)

                val headers = buildString {
                    append("HTTP/1.1 $status\r\n")
                    append("Content-Type: application/json; charset=utf-8\r\n")
                    append("Content-Length: ${bodyBytes.size}\r\n")
                    append("Connection: close\r\n")
                    append("\r\n")
                }

                output.write(headers.toByteArray(StandardCharsets.UTF_8))
                output.write(bodyBytes)
                output.flush()

            } catch (e: Exception) {
                // Ignore individual request failures.
            }
        }
    }

    private fun readRequest(input: BufferedInputStream): String {
        val out = StringBuilder()
        var previous = 0
        var current: Int

        while (true) {
            current = input.read()
            if (current == -1) break

            out.append(current.toChar())

            if (previous == '\r'.code && current == '\n'.code) {
                val text = out.toString()
                if (text.endsWith("\r\n\r\n")) {
                    return text
                }
            }

            previous = current
            if (out.length > 16_384) break
        }

        return out.toString()
    }
}
