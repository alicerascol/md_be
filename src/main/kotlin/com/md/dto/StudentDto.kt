package com.md.dto

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Email

@Entity
@Table(name = "student")
data class StudentDto(
    @Id
    @Column(name = "student_id")
    val student_id: UUID,

    val faculty_id: UUID,

    val documents_link: String,

    @Email(message = "Email should be valid")
    val email: String,

    val status: String
)
