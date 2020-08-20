package com.md.dto

import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email

@Entity
@Table(name = "student")
data class StudentDto(
    @Id
    @Column(name = "student_id")
    val student_id: UUID = UUID.randomUUID(),

    @OneToMany
    val faculty_id: List<FacultyDto>,

    val documents_link: String,

    @Email(message = "Email should be valid")
    val email: String,

    val status: StudentStatus
)

enum class StudentStatus { SIGNED_UP, VERIFIED, REGISTERED}
