package com.md.repository

import com.md.dto.Student
import org.springframework.data.repository.CrudRepository
import java.util.*


interface StudentRepository : CrudRepository<Student, UUID>
