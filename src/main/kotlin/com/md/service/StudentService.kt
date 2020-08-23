package com.md.service

import com.md.dto.*
import com.md.repository.StudentRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Component
class StudentService(
    private val studentRepository: StudentRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentService::class.java)
    }

    fun addNewStudent(studentDto: StudentDto, faculty: Faculty, studentsDocuments: MultipartFile):  Optional<Student> {
        LOGGER.info("addNewStudent")
        azureBlobStorageService.saveStudentsDocuments(faculty.container_name, studentsDocuments)
        return Optional.of(studentRepository.save(studentDto.toStudent()))
    }

    fun getStudents(): Optional<List<Student>> = Optional.of(studentRepository.findAll())

    fun getStudent(studentId: UUID): Optional<Student> = studentRepository.findById(studentId)

    fun updateStudentObject(student: Student, status: String): Optional<Student> {
        LOGGER.info("updateStudentObject")
        student.status = StudentStatus.valueOf(status)
        return Optional.of(studentRepository.save(student))
    }

    fun getStudentsFiltered(faculty_id: UUID, studentStatus: String): Optional<List<Student>> {
        return Optional.of(studentRepository.findByFacultyIdsAndStatus(faculty_id, studentStatus))
    }

}