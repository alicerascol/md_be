package com.md.dto

import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name = "faculty")
data class Faculty(

    @Id @GeneratedValue(generator = "system-uuid")
    val faculty_id: UUID = UUID.randomUUID(),

    @NotNull
    val faculty_name: String,

    @Email(message = "Email should be valid")
    @NotNull
    val faculty_email: String,

    @NotNull
    val faculty_pass: String,

    @NotNull
    val university: String,

    val JSON_blob_storage_link: String,

    val container_name: String
)

data class FacultyDto(

    @ApiModelProperty(notes = "faculty_name", required = true)
    @field: NotEmpty
    val faculty_name: String,

    @Email(message = "Email should be valid")
    @ApiModelProperty(notes = "faculty_email", required = true)
    @field: NotEmpty
    val faculty_email: String,

    @ApiModelProperty(notes = "faculty_pass", required = true)
    @field: NotEmpty
    val faculty_pass: String,

    @ApiModelProperty(notes = "university", required = true)
    @field: NotEmpty
    val university: String
)


fun FacultyDto.toFaculty(): Faculty {
    val f = this
    return Faculty(
        faculty_name = f.faculty_name,
        faculty_email = f.faculty_email,
        faculty_pass = f.faculty_pass,
        university = f.university,
        JSON_blob_storage_link =  "",
        container_name =  ""
    )
}