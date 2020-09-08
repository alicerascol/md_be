package com.md.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email

@Entity
@Table(name = "student")
data class Student(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "stud_fac_table",
        joinColumns = [JoinColumn(name = "studid", referencedColumnName = "id",  nullable = true)],
        inverseJoinColumns = [JoinColumn(name = "facid", referencedColumnName = "id")]
    )
    @JsonBackReference
    val faculties: MutableList<Faculty> = mutableListOf(),

    @Email(message = "Email should be valid")
    val email: String,

    val firstName: String,

    val lastName: String,

    val father_initials: String,

    val citizenship: String,

    val phone: String,

    val director: String

)

data class StudentDto(

    @Email(message = "Email should be valid")
    val email: String,

    val firstName: String,

    val lastName: String,

    val father_initials: String,

    val citizenship: String,

    val phone: String,

    var director: String,

    var faculties: MutableList<Faculty>? = mutableListOf()
)

enum class StudentStatus { REGISTERED, NEED_WORK, DOCUMENTS_RESENT , VERIFIED, ADMITTED}

fun StudentDto.toStudent(): Student {
    val s = this
    return Student(
        email = s.email,
//        status = StudentStatus.valueOf("REGISTERED"),
        faculties = s.faculties!!,
        firstName = s.firstName,
        lastName = s.lastName,
        father_initials = s.father_initials,
        citizenship = s.citizenship,
        phone = s.phone,
        director = s.director
    )
}

data class StudentWithStatus (
    val id: UUID = UUID.randomUUID(),

    val email: String,

    val firstName: String,

    val lastName: String,

    val father_initials: String,

    val citizenship: String,

    val phone: String,

    val director: String,

    var status: StudentStatus
)