package com.md.dto

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "faculty")
data class FacultyDto(
    @Id
    @Column(name = "faculty_id")
    val faculty_id: UUID = UUID.randomUUID(),

    val faculty_name: String,

    val university: String,

    val JSON_blob_storage_link: String,

    val container_name: String
)
