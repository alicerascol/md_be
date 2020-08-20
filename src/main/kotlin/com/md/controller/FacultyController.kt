package com.md.controller

import com.md.dto.ErrorDto
import com.md.dto.FacultyDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.md.service.FacultyService
import java.util.*


@RestController
@RequestMapping("/faculties")
@Api(tags = ["Universities"])
class FacultyController (val service: FacultyService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FacultyController::class.java)
    }

    @GetMapping
    @ApiOperation(value = "Get all faculties")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "Faculty", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun getAllUniversities(): ResponseEntity<*> {
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }

    @PostMapping("/{faculty_id}")
    @ApiOperation(value = "Add details")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Details about faculty added", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun addDetails(@PathVariable faculty_id: String): ResponseEntity<*> {
        LOGGER.info("start add details to a faculty")
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }

    @PostMapping
    @ApiOperation(value = "Add new faculty")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Add a new faculty", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun addNewFaculty(): ResponseEntity<*> {
        LOGGER.info("add a new faculty")
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }

    @PostMapping("/{faculty_id}/container")
    @ApiOperation(value = "Create Azure Container")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Create Azure Container for faculty", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun createAzureContainerForFaculty(@PathVariable faculty_id: String): ResponseEntity<*> {
        LOGGER.info("start creating a new container for the faculty %s", faculty_id)

//        return service.getFaculty(UUID.fromString(faculty_id)).map { faculty ->
//            service.createAzureContainer(faculty.container_name)
            service.createAzureContainer("containername")
//            ResponseEntity(faculty.container_name, HttpStatus.CREATED)
            return ResponseEntity("faculty.container_name", HttpStatus.CREATED)
//        }.orElse(errorResponse("Party not found", HttpStatus.NOT_FOUND) as ResponseEntity<String>?) as ResponseEntity<*>

    }

    @PostMapping("/{faculty_id}/login")
    @ApiOperation(value = "Add new faculty")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Add a new faculty", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun login(@PathVariable faculty_id: String): ResponseEntity<*> {
        LOGGER.info("add a new faculty")
        //each faculty will add an update JSON file in Azure Blob Storage
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }

    private fun errorResponse(message: String, httpStatus: HttpStatus): ResponseEntity<*> =
        ResponseEntity(ErrorDto(message), httpStatus)
}
