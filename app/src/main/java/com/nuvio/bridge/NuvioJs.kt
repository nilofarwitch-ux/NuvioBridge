package com.nuvio.bridge

import com.dokar.quickjs.QuickJs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object NuvioJs {

    fun test(): String = runBlocking(Dispatchers.Default) {
        val js = QuickJs.create(Dispatchers.Default)

        try {
            val value = js.evaluate<Int>("40 + 2")
            """{"ok":true,"runtime":"quickjs","result":$value}"""
        } finally {
            js.close()
        }
    }
}
