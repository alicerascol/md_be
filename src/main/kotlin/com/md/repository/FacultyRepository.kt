package com.md.repository

import com.md.model.Faculty
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface FacultyRepository : JpaRepository<Faculty, UUID> {
    fun findByEmail(email: String): Optional<Faculty>
}
