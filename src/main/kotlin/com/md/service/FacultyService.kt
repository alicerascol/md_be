package com.md.service

import com.md.dto.Faculty
import com.md.dto.FacultyDto
import com.md.dto.toFaculty
import com.md.repository.FacultyRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Component
class FacultyService(
    private val facultyRepository: FacultyRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FacultyService::class.java)
    }

    private val localPath = "/Users/arascol/Documents/alice/disertatie/be/data/config"


    fun addNewFaculty(facultyDto: FacultyDto):  Optional<Faculty> {
        LOGGER.info("Add new faculy")
        return Optional.of(facultyRepository.save(facultyDto.toFaculty()))
    }

    fun getFaculties(): Optional<List<Faculty>> = Optional.of(facultyRepository.findAll())

    fun getFacultyById(facultyId: UUID): Optional<Faculty> = facultyRepository.findById(facultyId)

    fun getFacultyByEmail(facultyEmail: String): Optional<Faculty> = facultyRepository.findByEmail(facultyEmail)

    fun updateFacultyObject(faculty: Faculty, configFileName: String, jsonBlobStorageLink: String): Optional<Faculty> {
        LOGGER.info("updateFacultyObject")
        faculty.JSON_blob_storage_link = jsonBlobStorageLink
        faculty.config_file_name = configFileName
        return Optional.of(facultyRepository.save(faculty))
    }

    fun createContainerIfNotExists(containerName: String) {
        azureBlobStorageService.createContainerIfNotExists(containerName)
        LOGGER.info("Container {} created", containerName)
    }

    fun uploadNewDetailsFileToContainer(detailsFacultyFile: MultipartFile, containerName: String): String {
        LOGGER.info("uploadNewDetailsFileToContainer")
        return azureBlobStorageService.uploadNewDetailsFileToContainer(detailsFacultyFile, containerName)
    }

    fun getJsonForFaculty(containerName: String, configFileName: String) {
        LOGGER.info("getJsonForFaculty")
        azureBlobStorageService.getJsonForFaculty(containerName, configFileName)
    }

    fun readFileDirectlyAsText(configFileName: String): String {
        LOGGER.info("readFileDirectlyAsText")
        return File(localPath + configFileName).readText(Charsets.UTF_8)
    }

}