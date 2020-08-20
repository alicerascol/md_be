package com.md.service

import com.md.dto.FacultyDto
import com.md.repository.FacultyRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.springframework.stereotype.Component
import java.util.*

@Component
class FacultyService(
    private val facultyRepository: FacultyRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    fun getFaculty(facultyId: UUID): Optional<FacultyDto> = facultyRepository.findById(facultyId)

    fun createAzureContainer(containerName: String) {
        azureBlobStorageService.createContainer(containerName)
    }

}