package com.adriano.sharetheimage.ui.shared

import kotlin.coroutines.cancellation.CancellationException

inline fun <R> runSuspendCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e // Re-throw CancellationException
    } catch (e: Throwable) {
        Result.failure(e)
    }
}