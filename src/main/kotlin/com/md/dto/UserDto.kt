package com.md.dto

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "student")
data class UserDto(
    @Id
    @Column(name = "user_id")
    val user_id: UUID = UUID.randomUUID(),

    val faculty_id: UUID,

    val user_name: String,

    val user_pass: String
)