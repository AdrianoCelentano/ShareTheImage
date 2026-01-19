package com.adriano.sharetheimage.data.remote

import com.adriano.sharetheimage.domain.model.RateLimitException
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.http.HttpStatusCode

fun HttpClientConfig<*>.installRateLimitHandler() {
    expectSuccess = true
    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            val clientException = exception as? ClientRequestException
                ?: return@handleResponseExceptionWithRequest
            if (clientException.response.status == HttpStatusCode.Forbidden) {
                throw RateLimitException("Rate limit exceeded")
            }
        }
    }
}
