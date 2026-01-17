package com.adriano.sharetheimage.debug

import android.content.Context
import java.io.File

object MockConfig {
    private const val FILE_NAME = "mock_mode"

    enum class Mode {
        NONE,
        ERROR_403,
        EMPTY_LIST
    }

    fun getMode(context: Context): Mode {
        // Disabling file read to prevent Main Thread I/O violation/Crashing on startup.
        // TODO: Move I/O to background thread or allow Disk Reads.
        return Mode.NONE

        val dir = context.getExternalFilesDir(null)
        if (dir == null) {
            android.util.Log.e("MockConfig", "External files dir is null")
            return Mode.NONE
        }
        val file = File(dir, FILE_NAME)
        android.util.Log.d("MockConfig", "Looking for config at: ${file.absolutePath}")
        
        return try {
            if (!file.exists()) {
                 android.util.Log.d("MockConfig", "File not found.")
                 return Mode.NONE
            }
            val content = file.readText().trim()
            android.util.Log.d("MockConfig", "Read mode: $content")
            Mode.valueOf(content)
        } catch (e: Exception) {
            android.util.Log.e("MockConfig", "Error reading mode", e)
            Mode.NONE
        }
    }
}
