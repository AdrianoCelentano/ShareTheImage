package com.adriano.sharetheimage.data.remote.mock

import android.os.Bundle

object MockConfig {
    var mode: Mode = Mode.None

    enum class Mode {
        None,
        Success,
        ErrorRateLimit,
        ErrorGeneral,
        EmptyList
    }

    fun loadFromIntent(arguments: Bundle?) {
        if (arguments == null) return
        val modeString = arguments.getString("mockMode") ?: return
        runCatching { mode = Mode.valueOf(modeString) }
    }
}
