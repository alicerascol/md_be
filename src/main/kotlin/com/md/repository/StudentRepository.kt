package com.md.repository

import com.md.model.Student
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface StudentRepository : JpaRepository<Student, UUID> {
    fun findByEmail(email: String): Optional<Student>
}