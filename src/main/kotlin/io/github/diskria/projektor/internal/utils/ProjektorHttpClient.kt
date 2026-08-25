package io.github.diskria.projektor.internal.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.Duration.Companion.minutes

internal object ProjektorHttpClient {
    val client by lazy {
        HttpClient(CIO) {
            install(HttpTimeout) {
                connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                socketTimeoutMillis = 2.minutes.inWholeMilliseconds
                requestTimeoutMillis = 5.minutes.inWholeMilliseconds
            }
            defaultRequest {
                header(HttpHeaders.UserAgent, "Projektor/8.0.2")
            }
        }
    }
}
