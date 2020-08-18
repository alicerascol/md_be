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
    val faculty_id: UUID,

    val JSON_blob_storage_link: String
)
