package com.md.dto

import com.md.service.Utils
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name = "faculty")
data class Faculty(

    @Id @GeneratedValue(generator = "system-uuid")
    @Column(unique=true)
    val id: UUID = UUID.randomUUID(),

    @NotNull
    @Column(unique=true)
    val name: String,

    @Email(message = "Email should be valid")
    @NotNull
    @Column(unique=true)
    val email: String,

    @NotNull
    val password: String,

    @NotNull
    val university: String,

    var JSON_blob_storage_link: String,

    var config_file_name: String,

    val container_name: String
)

data class FacultyDto(
    @ApiModelProperty(notes = "name", required = true)
    @field: NotEmpty
    val name: String,

    @Email(message = "Email should be valid")
    @ApiModelProperty(notes = "email", required = true)
    @field: NotEmpty
    val email: String,

    @ApiModelProperty(notes = "password", required = true)
    @field: NotEmpty
    val password: String,

    @ApiModelProperty(notes = "university", required = true)
    @field: NotEmpty
    val university: String
)


fun FacultyDto.toFaculty(): Faculty {
    val f = this
    return Faculty(
        name = f.name,
        email = f.email,
        password = Utils.hashString(f.password),
        university = f.university,
        JSON_blob_storage_link =  "",
        config_file_name = "",
        container_name =  f.name.replace(" ","").toLowerCase()
    )
}