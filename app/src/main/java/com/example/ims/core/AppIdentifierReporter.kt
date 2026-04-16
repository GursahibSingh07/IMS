package com.example.ims.core

import com.example.ims.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object AppIdentifierReporter {
    private const val ENDPOINT = "https://project-tracker-0eju.onrender.com/api/data"

    fun sendIfConfigured() {
        val appIdentifier = BuildConfig.APPIDENTIFIER
        if (appIdentifier.isBlank()) return

        runCatching {
            val payload = "{\"appIdentifier\":\"$appIdentifier\"}"
            val connection = URL(ENDPOINT).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { output ->
                output.write(payload.toByteArray(StandardCharsets.UTF_8))
            }
            connection.inputStream.close()
            connection.disconnect()
        }
    }
}
