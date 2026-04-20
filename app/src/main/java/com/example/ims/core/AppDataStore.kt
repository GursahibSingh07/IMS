package com.example.ims.core

import android.content.Context
import com.example.ims.ui.screens.timetable.CourseAllocation
import com.example.ims.ui.screens.timetable.CourseInstance
import com.example.ims.ui.screens.timetable.TimetableRecord
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserCredentialRecord(
    val username: String,
    val password: String,
    val displayName: String,
    val role: Role,
    val institute: String,
    val email: String
)

data class StudentRecord(
    val username: String,
    val batch: String,
    val scholarship: String,
    val course: String,
    val stream: String,
    val cgpa: Double,
    val studentId: String,
    val phone: String,
    val address: String
)

data class AppDataFile(
    val users: List<UserCredentialRecord>,
    val students: List<StudentRecord>,
    val timetables: List<TimetableRecord>,
    val primaryTimetableId: String
)

private const val APP_DATA_FILE_NAME = "ims_app_data.json"

class AppDataStore(private val context: Context) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun load(): AppDataFile = withContext(Dispatchers.IO) {
        val file = context.filesDir.resolve(APP_DATA_FILE_NAME)
        if (!file.exists()) {
            val defaults = defaultAppData()
            file.writeText(gson.toJson(defaults))
            return@withContext defaults
        }

        val parsed = runCatching {
            gson.fromJson(file.readText(), AppDataFile::class.java)
        }.getOrNull()

        if (parsed == null) {
            val defaults = defaultAppData()
            file.writeText(gson.toJson(defaults))
            defaults
        } else {
            val normalized = normalizeLoadedData(parsed)
            if (normalized != parsed) {
                file.writeText(gson.toJson(normalized))
            }
            normalized
        }
    }

    suspend fun save(data: AppDataFile) = withContext(Dispatchers.IO) {
        val file = context.filesDir.resolve(APP_DATA_FILE_NAME)
        file.writeText(gson.toJson(data))
    }
}

private fun expectedUsers(): List<UserCredentialRecord> = listOf(
    UserCredentialRecord(
        username = "admin",
        password = "admin",
        displayName = "Ava Malik",
        role = Role.ACADEMIC_OFFICE,
        institute = "IMS Main Campus",
        email = "admin@ims.edu"
    ),
    UserCredentialRecord(
        username = "faculty1",
        password = "faculty1",
        displayName = "Dr. Aris Thorne",
        role = Role.FACULTY,
        institute = "Computer Science",
        email = "thorne@ims.edu"
    ),
    UserCredentialRecord(
        username = "faculty2",
        password = "faculty2",
        displayName = "Prof. Sarah Lee",
        role = Role.FACULTY,
        institute = "Digital Marketing",
        email = "lee@ims.edu"
    ),
    UserCredentialRecord(
        username = "faculty3",
        password = "faculty3",
        displayName = "Dr. James Bond",
        role = Role.FACULTY,
        institute = "Security Studies",
        email = "bond@ims.edu"
    ),
    UserCredentialRecord(
        username = "student1",
        password = "student1",
        displayName = "Jameson Miller",
        role = Role.STUDENT,
        institute = "Computer Science",
        email = "jameson@ims.edu"
    ),
    UserCredentialRecord(
        username = "student2",
        password = "student2",
        displayName = "Jamie Chen",
        role = Role.STUDENT,
        institute = "Digital Marketing",
        email = "jamie@ims.edu"
    ),
    UserCredentialRecord(
        username = "student3",
        password = "student3",
        displayName = "Robert Ross",
        role = Role.STUDENT,
        institute = "Fine Arts",
        email = "robert@ims.edu"
    )
)

private fun expectedStudents(): List<StudentRecord> = listOf(
    StudentRecord(
        username = "student1",
        batch = "2024",
        scholarship = "Merit",
        course = "B.Tech",
        stream = "Computer Science",
        cgpa = 9.1,
        studentId = "2024091",
        phone = "+1-202-555-0142",
        address = "12 Cedar Lane, Block A"
    ),
    StudentRecord(
        username = "student2",
        batch = "2023",
        scholarship = "Need-Based",
        course = "BBA",
        stream = "Digital Marketing",
        cgpa = 8.3,
        studentId = "2024102",
        phone = "+1-202-555-0167",
        address = "91 Willow Street, Unit 7"
    ),
    StudentRecord(
        username = "student3",
        batch = "2022",
        scholarship = "Sports",
        course = "BFA",
        stream = "Fine Arts",
        cgpa = 7.4,
        studentId = "2024118",
        phone = "+1-202-555-0119",
        address = "5 Orchid Park"
    )
)

private fun normalizeLoadedData(data: AppDataFile): AppDataFile {
    val canonicalUsers = expectedUsers()
    val canonicalStudents = expectedStudents()

    val currentUsernames = data.users.map { it.username }.toSet()
    val expectedUsernames = canonicalUsers.map { it.username }.toSet()
    val currentStudentUsernames = data.students.map { it.username }.toSet()
    val expectedStudentUsernames = canonicalStudents.map { it.username }.toSet()

    val updatedUsers = if (currentUsernames != expectedUsernames || data.users.size != 7) {
        canonicalUsers
    } else {
        data.users
    }

    val updatedStudents = if (currentStudentUsernames != expectedStudentUsernames || data.students.size != 3) {
        canonicalStudents
    } else {
        data.students
    }

    val updatedTimetables = data.timetables.map { timetable ->
        val allocationFixups = timetable.allocations.map { allocation ->
            val sourceTemplate = timetable.instances.find { it.id == allocation.templateId }
            val savedFaculty = allocation.faculty as String?
            val savedClassroom = allocation.classroom as String?
            val savedCourse = allocation.course as String?
            if (sourceTemplate != null) {
                allocation.copy(
                    faculty = if (savedFaculty.isNullOrBlank()) sourceTemplate.faculty else savedFaculty,
                    classroom = if (savedClassroom.isNullOrBlank()) sourceTemplate.classroom else savedClassroom,
                    course = if (savedCourse.isNullOrBlank()) sourceTemplate.course else savedCourse
                )
            } else {
                allocation
            }
        }

        if (timetable.name.equals("Main Timetable", ignoreCase = false)) {
            timetable.copy(name = "Timetable 1", allocations = allocationFixups)
        } else {
            timetable.copy(allocations = allocationFixups)
        }
    }

    return data.copy(
        users = updatedUsers,
        students = updatedStudents,
        timetables = updatedTimetables
    )
}

fun defaultAppData(): AppDataFile {
    val users = expectedUsers()

    val students = expectedStudents()

    val defaultTimetable = TimetableRecord(
        id = "tt-main",
        name = "Timetable 1",
        instances = emptyList<CourseInstance>(),
        allocations = emptyList<CourseAllocation>()
    )

    return AppDataFile(
        users = users,
        students = students,
        timetables = listOf(defaultTimetable),
        primaryTimetableId = defaultTimetable.id
    )
}

fun UserCredentialRecord.toUserProfile(): MockUserProfile = MockUserProfile(
    displayName = displayName,
    role = role,
    institute = institute,
    email = email,
    username = username
)
