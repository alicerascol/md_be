package com.md.repository

import com.md.dto.StudentDto
import org.springframework.data.repository.CrudRepository
import java.util.*


interface StudentRepository : CrudRepository<StudentDto, UUID>
