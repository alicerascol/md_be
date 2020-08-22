package com.md.service

import com.md.dto.Faculty
import com.md.dto.FacultyDto
import com.md.dto.toFaculty
import com.md.repository.FacultyRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.springframework.stereotype.Component
import java.util.*

@Component
class FacultyService(
    private val facultyRepository: FacultyRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    fun addNewFaculty(facultyDto: FacultyDto):  Optional<Faculty> {
        return Optional.of(facultyRepository.save(facultyDto.toFaculty()))
    }

    fun getFaculties(): Optional<List<Faculty>> = Optional.of(facultyRepository.findAll())

    fun getFaculty(facultyId: UUID): Optional<Faculty> = facultyRepository.findById(facultyId)

    fun createAzureContainer(containerName: String) {
        azureBlobStorageService.createContainer(containerName)
    }

}