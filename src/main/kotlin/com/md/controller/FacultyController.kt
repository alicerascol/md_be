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


@RestController("/faculties")
@Api(tags = ["Universities"])
class FacultyController (val service: FacultyService){

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FacultyController::class.java)
    }

    @GetMapping("/")
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

    @PostMapping("/{faculty_id}")
    @ApiOperation(value = "Add details")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Details about faculty added", response = FacultyDto::class),
            ApiResponse(code = 400, message = "Bad request"),
            ApiResponse(code = 500, message = "Internal error, try again later")]
    )
    fun login(@PathVariable faculty_id: String): ResponseEntity<*> {
        LOGGER.info("start add details to a faculty")
        //each faculty will add an update JSON file in Azure Blob Storage
        return errorResponse("Party not found", HttpStatus.NOT_FOUND)
    }

    private fun errorResponse(message: String, httpStatus: HttpStatus): ResponseEntity<Any> =
        ResponseEntity(ErrorDto(message), httpStatus)
}
