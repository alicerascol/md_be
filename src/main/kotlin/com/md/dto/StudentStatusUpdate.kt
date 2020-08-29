package com.md.dto

import java.util.*

data class StudentStatusUpdate (
    val status: String,
    val student_id: UUID
)