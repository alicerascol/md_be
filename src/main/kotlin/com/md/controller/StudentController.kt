package com.md.controller

import com.md.dto.ErrorDto
import com.md.dto.FacultyDto
import com.md.dto.StudentDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.md.service.StudentService


@RestController
@RequestMapping("/students")
@Api(tags = ["Students"])
class StudentController (val service: StudentService){

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentController::class.java)
    }

    @GetMapping
    @ApiOperation(value = "Get all students")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getAllStudents(): ResponseEntity<*> {
        LOGGER.info("entered get all students method")
        // should exist the possibility to filter the students
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }


    @PostMapping("/")
    @ApiOperation(value = "Sign up new student")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student", response = StudentDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun signUpNewStudent(@PathVariable student_id: String) {
        LOGGER.info("entered manual update for students' status")
        // add a new student to db
    }


    @PostMapping("/{student_id}/update")
    @ApiOperation(value = "Manual update for students' status")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun manualUpdateStudentStatus(@PathVariable student_id: String) {
        LOGGER.info("entered manual update for students' status")
        // update student in DB
        // send email to student
    }

    private fun errorResponse(message: String, httpStatus: HttpStatus): ResponseEntity<Any> =
        ResponseEntity(ErrorDto(message), httpStatus)
}
