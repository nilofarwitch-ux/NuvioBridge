package com.nuvio.bridge

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView
    private var server: LocalServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply {
            text = "Nuvio Bridge\nStarting..."
            textSize = 18f
            setPadding(48, 80, 48, 48)
        }
        setContentView(status)

        server = LocalServer(
            port = 8765,
            onStatus = { message ->
                runOnUiThread {
                    status.text = "Nuvio Bridge\n\n$message"
                }
            }
        ).also { it.start() }
    }

    override fun onDestroy() {
        server?.stop()
        server = null
        super.onDestroy()
    }
}
