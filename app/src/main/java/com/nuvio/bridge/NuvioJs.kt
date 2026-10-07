package com.nuvio.bridge

import com.dokar.quickjs.QuickJs

object NuvioJs {

    fun test(): String {
        val js = QuickJs.create()
        return try {
            val value = js.evaluate<Int>("40 + 2")
            """{"ok":true,"runtime":"quickjs","result":$value}"""
        } finally {
            js.close()
        }
    }
}
