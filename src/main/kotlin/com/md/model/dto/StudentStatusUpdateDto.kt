package com.md.model.dto

import java.util.*

data class StudentStatusUpdateDto (
    val status: String,
    val student_id: UUID
)