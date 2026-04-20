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

object MockDatabase {
    val users = listOf(
        // 1 Authorized User (Academic Office)
        MockUserProfile("Ava Malik", Role.ACADEMIC_OFFICE, "IMS Main Campus", "admin@ims.edu", "admin"),
        
        // 3 Faculty Users
        MockUserProfile("Dr. Aris Thorne", Role.FACULTY, "Computer Science", "thorne@ims.edu", "faculty1"),
        MockUserProfile("Prof. Sarah Lee", Role.FACULTY, "Digital Marketing", "lee@ims.edu", "faculty2"),
        MockUserProfile("Dr. James Bond", Role.FACULTY, "Security Studies", "bond@ims.edu", "faculty3"),
        
        // 3 Student Users
        MockUserProfile("Jameson Miller", Role.STUDENT, "Computer Science", "jameson@ims.edu", "student1"),
        MockUserProfile("Jamie Chen", Role.STUDENT, "Digital Marketing", "jamie@ims.edu", "student2"),
        MockUserProfile("Robert Ross", Role.STUDENT, "Fine Arts", "robert@ims.edu", "student3")
    )

    val students = users.filter { it.role == Role.STUDENT }
}

object MockAuthService {
    fun validateCredentials(username: String, password: String): MockUserProfile? {
        // password is same as username for simulation
        return MockDatabase.users.find { it.username == username && password == username }
    }
}