package com.md.controller

import com.md.model.Student
import com.md.model.dto.EmailDto
import com.md.service.FacultyService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.md.service.StudentService
import com.md.service.email.EmailService
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.validation.Valid
import kotlin.collections.HashMap

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/students")
@Api(tags = ["Students"])
class StudentController (val service: StudentService,
                         val facultyService: FacultyService,
                         val emailService: EmailService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentController::class.java)
    }

    @GetMapping
    @ApiOperation(value = "Get all students")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Get all students"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getAllStudents(): ResponseEntity<*> {
        LOGGER.info("Get all students")
        return service.getStudents()
            .map { studentsList -> ResponseEntity<Any>(studentsList, HttpStatus.OK) }
            .orElse(ResponseEntity("Students not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/filtered/{status}")
    @ApiOperation(value = "Get all students filtered by faculty and status")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Get all students filtered by faculty and status"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getAllStudentsFiltered(@PathVariable faculty_id: UUID,
                               @PathVariable status: String): ResponseEntity<Any>? {
        LOGGER.info("Get all students filtered by faculty and status")

        return facultyService.getFacultyById(faculty_id)
            .map { faculty ->
                val studentList = service.getStudentsFiltered(faculty, status)
                LOGGER.info("students retrieved successfully")
                ResponseEntity<Any>(studentList, HttpStatus.OK) }
            .orElse(ResponseEntity(HttpStatus.NOT_FOUND))

    }

    @GetMapping("/{student_id}")
    @ApiOperation(value = "Get student by id")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getStudentById(@PathVariable student_id: UUID): ResponseEntity<*> {
        LOGGER.info("Get student by id")
        return service.getStudent(student_id)
            .map { student -> ResponseEntity<Any>(student, HttpStatus.OK)  }
            .orElse(ResponseEntity("Students not found", HttpStatus.NOT_FOUND))
    }


    @PostMapping("/{faculty_id}/register")
    @ApiOperation(value = "Sign up new student")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun signUpNewStudent(@RequestPart ( "studentDocuments") studentDocuments: List<MultipartFile>,
                         @PathVariable faculty_id: UUID
    ):  ResponseEntity<*> {
        LOGGER.info("Sign up new student")

        return facultyService.getFacultyById(faculty_id)
            .map { faculty ->
                val student = service.addNewStudent(studentDocuments, faculty)
                LOGGER.info("student added successfully")
                ResponseEntity<Any>(student, HttpStatus.CREATED)
            }.orElse(ResponseEntity("Required faculty not found", HttpStatus.NOT_FOUND))

    }

    @PostMapping("/{faculty_id}/{student_id}/update_documents")
    @ApiOperation(value = "Update documents of existing student")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun updateDocumentsForExistingStudent(@RequestPart ( "studentDocuments") studentDocuments: List<MultipartFile>,
                         @PathVariable faculty_id: UUID,  @PathVariable student_id: UUID
    ):  ResponseEntity<*> {
        LOGGER.info("Update documents of existing student")

        return facultyService.getFacultyById(faculty_id)
            .map { faculty ->
                val student: List<Student> = faculty.students!!.filter { student -> student.id == student_id  }
                service.updateDocumentsForExistingStudent(studentDocuments, faculty, student[0])
                LOGGER.info("student added successfully")
                ResponseEntity<Any>(student, HttpStatus.CREATED)
            }.orElse(ResponseEntity("Required faculty not found", HttpStatus.NOT_FOUND))

    }

    @PostMapping("/{student_id}/update/{status}")
    @ApiOperation(value = "Manual update for students' status")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun manualUpdateStudentStatus(@PathVariable student_id: UUID,
                                  @PathVariable status: String):  ResponseEntity<*> {
        LOGGER.info("entered manual update for students' status")
        return service.getStudent(student_id)
            .map { student ->
                service.updateStudentObject(student, status)
                LOGGER.info("student updated successfully")
                ResponseEntity<Any>(student, HttpStatus.OK)
            }
            .orElse(ResponseEntity("Student not found", HttpStatus.NOT_FOUND))
    }

    @PostMapping("/{student_id}/email")
    @ApiOperation(value = "Send status email for student")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun sendEmailToTheStudent(@PathVariable student_id: UUID,
                                @Valid @RequestBody emailDto: EmailDto
    ):  ResponseEntity<*>  {
        LOGGER.info("Send status email for student")
        return service.getStudent(student_id)
            .map { student ->
                emailService.sendEmail(student.email, emailDto)
                LOGGER.info("email sent successfully")
                ResponseEntity<Any>("Email sent to the student " + student.email, HttpStatus.OK) }
            .orElse(ResponseEntity("Student not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/{student_id}/documents")
    @ApiOperation(value = "Send status email for student")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getStudentDocuments(@PathVariable faculty_id: UUID, @PathVariable student_id: UUID):  ResponseEntity<*>  {
        LOGGER.info("Get students' documents")
        return facultyService.getFacultyById(faculty_id)
            .map {faculty ->
                service.getStudent(student_id)
                    .map { student ->
                        val blobDetails = service.getStudentDocuments(faculty.container_name, student)
                        val response = HashMap<String, HashMap<String, String>>()
                        val dummyValue = HashMap<String, String>()
                        dummyValue["email"] = student.email
                        response["email"] = dummyValue
                        response["documents"] = blobDetails
                        ResponseEntity<Any>(response, HttpStatus.OK)
                    }
                    .orElse(ResponseEntity("Student not found", HttpStatus.NOT_FOUND))
            }.orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/export")
    @ApiOperation(value = "Export excel with students' details")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun exportExcelWithStudentsDetails(@PathVariable faculty_id: UUID):  ResponseEntity<*>  {
        LOGGER.info("Export excel with students' details")
        return facultyService.getFacultyById(faculty_id)
            .map {faculty ->
                val link = service.generateExcel(faculty)
                ResponseEntity<Any>(link, HttpStatus.OK)
            }.orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/export/data")
    @ApiOperation(value = "Export excel with students' details")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "student"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun exportDataStudentsForExcel(@PathVariable faculty_id: UUID):  ResponseEntity<*>  {
        LOGGER.info("Export excel with students' details")
        return facultyService.getFacultyById(faculty_id)
            .map {faculty ->
                val studentsData = service.generateDataForExcel(faculty)
                ResponseEntity<Any>(studentsData, HttpStatus.OK)
            }.orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

}
