package com.md.repository

import com.md.model.Student
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface StudentRepository : JpaRepository<Student, UUID> {
//    fun findByFacultyIdsAndStatus(facultyId: UUID, status: String): List<Student>
}