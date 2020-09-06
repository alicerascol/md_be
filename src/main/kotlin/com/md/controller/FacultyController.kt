package com.md.controller

import com.md.model.FacultyDto
import com.md.model.dto.UserDto
import com.md.service.FacultyService
import com.md.service.Utils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/faculties")
@Api(tags = ["Faculties"])
class FacultyController(val service: FacultyService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FacultyController::class.java)
    }

    @PostMapping
    @ApiOperation(value = "Register a new faculty")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "Register a new faculty"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun addNewFaculty(@Valid @RequestBody facultyDto: FacultyDto): ResponseEntity<Any> {
        LOGGER.info("Add a new faculty")
        return service.addNewFaculty(facultyDto)
            .map { faculty -> ResponseEntity<Any>(faculty, HttpStatus.CREATED) }
            .orElse(ResponseEntity(HttpStatus.BAD_REQUEST))
    }

    @GetMapping("/{faculty_id}")
    @ApiOperation(value = "Get faculty by faculty_id")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Get faculty by faculty_id"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getFaculty(@PathVariable faculty_id: UUID): ResponseEntity<Any> {
        LOGGER.info("Get faculty by faculty_id")
        return service.getFacultyById(faculty_id)
            .map { faculty -> ResponseEntity<Any>(faculty, HttpStatus.OK) }
            .orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/landing_page")
    @ApiOperation(value = "Get faculty by faculty_id")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Get faculty by faculty_id"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getFacultyLandingPage(@PathVariable faculty_id: UUID): ResponseEntity<Any> {
        LOGGER.info("Get faculty by faculty_id")
        return service.getFacultyById(faculty_id)
            .map { faculty -> ResponseEntity<Any>(faculty.landing_page_link, HttpStatus.OK) }
            .orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping
    @ApiOperation(value = "Get all faculties")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Faculties"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getAllFaculties(): ResponseEntity<Any> {
        LOGGER.info("Get the faculty list")
        return service.getFaculties()
            .map { facultyList -> ResponseEntity<Any>(facultyList, HttpStatus.OK) }
            .orElse(ResponseEntity("Faculties not found", HttpStatus.NOT_FOUND))
    }

    @PostMapping("/{faculty_id}/details")
    @ApiOperation(value = "Add details for a faculty")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Add details for a faculty"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun addDetailsForFaculty(@PathVariable faculty_id: UUID,
                             @RequestPart ( "detailsFacultyFile") detailsFacultyFile: MultipartFile): ResponseEntity<*> {
        LOGGER.info("start adding details to a faculty")

        //get faculty
        return service.getFacultyById(faculty_id)
            .map { faculty ->
                    // create container for the faculty if it doesnt exist
                    service.createContainerIfNotExists(faculty.container_name)
                    // upload file to container
                    val jsonBlobStorageLink = service.uploadNewDetailsFileToContainer(detailsFacultyFile, faculty.container_name)
                    // update faculty object with the containers name and the link for the blob
                    val newFacultyObject = service.updateFacultyObject(faculty, detailsFacultyFile.originalFilename, jsonBlobStorageLink)
                    ResponseEntity(newFacultyObject, HttpStatus.OK)
            }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/details/download")
    @ApiOperation(value = "Download JSON details for faculty by faculty id")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Download JSON details for faculty by faculty id"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun downloadFacultyDetailsFile(@PathVariable faculty_id: UUID): ResponseEntity<*> {
        LOGGER.info("Download JSON details for faculty by faculty id")

        return service.getFacultyById(faculty_id)
            .map { faculty ->
                service.getJsonForFaculty(faculty.container_name, faculty.config_file_name)
                ResponseEntity("Document downloaded", HttpStatus.OK) }
            .orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @GetMapping("/{faculty_id}/details")
    @ApiOperation(value = "Get JSON details for faculty by faculty id")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Get JSON details for faculty by faculty id"),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getFacultyDetails(@PathVariable faculty_id: UUID): ResponseEntity<*> {
        LOGGER.info("Get JSON details for faculty by faculty id")

        return service.getFacultyById(faculty_id)
            .map { faculty ->
                val facultyDetails = service.readFileDirectlyAsText(faculty.config_file_name)
                ResponseEntity(facultyDetails, HttpStatus.OK) }
            .orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

    @PostMapping("/login")
    @ApiOperation(value = "Login faculty")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Add a new faculty", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun login(@Valid @RequestBody userDto: UserDto): ResponseEntity<*> {
        LOGGER.info("Login faculty")

        return service.getFacultyByEmail(userDto.username)
            .map { faculty ->
                if(Utils.hashString(userDto.password) == faculty.password) {
                    LOGGER.info("Faculty logged in")
                    ResponseEntity<Any>(faculty, HttpStatus.OK)
                }
                else ResponseEntity("Incorrect password", HttpStatus.BAD_REQUEST)
            }
            .orElse(ResponseEntity("Faculty not found", HttpStatus.NOT_FOUND))
    }

}
