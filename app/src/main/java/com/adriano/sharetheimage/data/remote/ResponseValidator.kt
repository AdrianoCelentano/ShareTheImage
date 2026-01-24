package com.adriano.sharetheimage.data.remote

import com.adriano.sharetheimage.domain.model.RateLimitException
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.http.HttpStatusCode

fun HttpClientConfig<*>.installRateLimitHandler() {
    HttpResponseValidator {
        validateResponse { response ->
            val remaining = response.headers["X-Ratelimit-Remaining"]
            if (response.status == HttpStatusCode.Forbidden || remaining == "0") {
                throw RateLimitException("Unsplash Rate Limit Exceeded.")
            }
        }
    }
}
