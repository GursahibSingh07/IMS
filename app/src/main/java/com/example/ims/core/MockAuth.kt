package com.example.ims.core

data class MockUserProfile(
    val displayName: String,
    val role: String,
    val institute: String,
    val email: String
)

object MockAuthService {
    private const val TEMP_USERNAME = "ims_admin"
    private const val TEMP_PASSWORD = "ims@123"

    private val mockProfile = MockUserProfile(
        displayName = "Ava Malik",
        role = "Administrator",
        institute = "IMS Demo Campus",
        email = "admin@ims.local"
    )

    fun validateCredentials(username: String, password: String): MockUserProfile? {
        val isValid = username.trim() == TEMP_USERNAME && password == TEMP_PASSWORD
        return if (isValid) mockProfile else null
    }

    fun temporaryCredentials(): Pair<String, String> = TEMP_USERNAME to TEMP_PASSWORD
}