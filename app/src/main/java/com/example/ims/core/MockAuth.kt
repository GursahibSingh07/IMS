package com.example.ims.core

data class MockUserProfile(
    val displayName: String,
    val role: Role,
    val institute: String,
    val email: String,
    val username: String
)

enum class Role {
    ACADEMIC_OFFICE,
    FACULTY,
    STUDENT
}