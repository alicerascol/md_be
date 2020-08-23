package com.md.dto

import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

@Entity
@Table(name = "student")
data class Student(

    @Id
    val id: UUID = UUID.randomUUID(),

    @OneToMany
    val facultyIds: List<Faculty>,

    var documents_link: String,

    @Email(message = "Email should be valid")
    val email: String,

    var status: StudentStatus
)

data class StudentDto(

    val facultyIds: List<FacultyDto>,

    @Email(message = "Email should be valid")
    val email: String,

    @field: Pattern(regexp = "REGISTERED|NEED_WORK|VERIFIED")
    val status: String
)

enum class StudentStatus { REGISTERED, NEED_WORK, VERIFIED}

fun StudentDto.toStudent(): Student {
    val s = this
    return Student(
        email = s.email,
        status = StudentStatus.valueOf(s.status),
        facultyIds = s.facultyIds.map { facultyDto: FacultyDto -> facultyDto.toFaculty() },
        documents_link =  ""
    )
}