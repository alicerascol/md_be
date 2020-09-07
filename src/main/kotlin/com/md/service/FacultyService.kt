package com.md.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.md.model.Faculty
import com.md.model.FacultyDto
import com.md.model.dto.KeyValueUpdateObjectDto
import com.md.model.toFaculty
import com.md.repository.FacultyRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.OutputStreamWriter
import java.util.*


@Component
class FacultyService(
    private val facultyRepository: FacultyRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FacultyService::class.java)
    }

    @Value("\${localPath}")
    private val localPath: String? = null

    fun addNewFaculty(facultyDto: FacultyDto): Optional<Faculty> {
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

    fun uploadNewDetailsFileToContainer(detailsFacultyFile: File, containerName: String): String {
        LOGGER.info("uploadNewDetailsFileToContainer")
        return azureBlobStorageService.uploadNewDetailsFileToContainer(detailsFacultyFile, containerName)
    }

    fun getJsonForFaculty(containerName: String, configFileName: String) {
        LOGGER.info("getJsonForFaculty")
        azureBlobStorageService.getJsonForFaculty(containerName, configFileName)
    }

    fun readFileDirectlyAsText(configFileName: String): String {
        return Utils.readFileDirectlyAsText(configFileName, localPath!!)
    }

    fun updateConfigFileForFaculty(keyValueUpdateObjectDto: KeyValueUpdateObjectDto, faculty: Faculty): String {
        LOGGER.info("updateConfigFileForFaculty")
        val fileContent = Utils.readFileDirectlyAsText(faculty.config_file_name, localPath!!)
        val convertedObject: JsonObject = Gson().fromJson(fileContent, JsonObject::class.java)
        if (convertedObject[keyValueUpdateObjectDto.key] !== null) {
            convertedObject.remove(keyValueUpdateObjectDto.key)
            convertedObject.addProperty(keyValueUpdateObjectDto.key, keyValueUpdateObjectDto.value)
        }
        val newFile: File = writeNewFile(convertedObject, faculty.config_file_name)
        uploadNewDetailsFileToContainer(newFile, faculty.container_name)
        return convertedObject.toString()
    }

    fun writeNewFile(convertedObject: JsonObject, config_file_name: String): File {
        LOGGER.info("writeNewFile")
            // delete file if exists
        val dir = File(localPath)
        if (dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                File(dir, children[i]).delete()
            }
        }
        val fileName = localPath  + config_file_name
        val writtenFile = File(fileName)

        writtenFile.bufferedWriter().use { out ->
            out.write(convertedObject.toString())
        }
        LOGGER.info("writeNewFile")
        return writtenFile
    }
}