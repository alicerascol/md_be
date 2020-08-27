package com.md.dto

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

@Entity
@Table(name = "student")
data class Student(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_faculty",
        joinColumns = [JoinColumn(name = "student_id", referencedColumnName = "id",  nullable = true)],
        inverseJoinColumns = [JoinColumn(name = "faculty_id", referencedColumnName = "id")]
    )
    @JsonBackReference
    val faculties: MutableList<Faculty> = mutableListOf(),

    var documents_link: String,

    @Email(message = "Email should be valid")
    val email: String,

    val firstName: String,

    val lastName: String,

    val father_initials: String,

    val citizenship: String,

    val phone: String,

    var status: StudentStatus
)

data class StudentDto(

    @Email(message = "Email should be valid")
    val email: String,

    val firstName: String,

    val lastName: String,

    val father_initials: String,

    val citizenship: String,

    val phone: String,

    var faculties: MutableList<Faculty>? = mutableListOf()
)

enum class StudentStatus { REGISTERED, NEED_WORK, VERIFIED}

fun StudentDto.toStudent(): Student {
    val s = this
    return Student(
        email = s.email,
        status = StudentStatus.REGISTERED,
        faculties = s.faculties!!,
        documents_link =  "",
        firstName = s.firstName,
        lastName = s.lastName,
        father_initials = s.father_initials,
        citizenship = s.citizenship,
        phone = s.phone
    )
}