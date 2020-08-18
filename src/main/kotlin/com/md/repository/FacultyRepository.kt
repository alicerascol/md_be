package com.md.repository

import com.md.dto.FacultyDto
import org.springframework.data.repository.CrudRepository
import java.util.*


interface FacultyRepository : CrudRepository<FacultyDto, UUID>
