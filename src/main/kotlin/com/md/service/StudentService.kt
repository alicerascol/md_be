package com.md.service

import com.md.dto.*
import com.md.repository.StudentRepository
import com.md.service.blobStorage.AzureBlobStorageService
import org.json.JSONArray
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*
import org.json.JSONObject;

@Component
class StudentService(
    private val studentRepository: StudentRepository,
    private val azureBlobStorageService: AzureBlobStorageService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentService::class.java)
    }
    private val localPath = "/Users/arascol/Documents/alice/disertatie/be/data/"

    fun addNewStudent(studentDocuments: List<MultipartFile>, faculty: Faculty): Student? {
        LOGGER.info("addNewStudent")
        var studentDto: StudentDto? = null
        studentDocuments.map {
            studentDocument ->
            if(studentDocument.originalFilename.contains("studentInfo")) {
                studentDto = parseFileToGetInfo(studentDocument, faculty)
            }
        }
        val studentDirector = studentDto?.firstName?.toLowerCase() + studentDto?.lastName?.toLowerCase() + studentDto?.father_initials?.toLowerCase();
        azureBlobStorageService.saveStudentDocuments(faculty.container_name, studentDocuments, studentDirector)
        return studentRepository.save(studentDto?.toStudent())
    }

    fun getStudents(): Optional<List<Student>> = Optional.of(studentRepository.findAll())

    fun getStudent(studentId: UUID): Optional<Student> = studentRepository.findById(studentId)

    fun updateStudentObject(student: Student, status: String): Optional<Student> {
        LOGGER.info("updateStudentObject")
        student.status = StudentStatus.valueOf(status)
        return Optional.of(studentRepository.save(student))
    }

//    fun getStudentsFiltered(faculty_id: UUID, studentStatus: String): Optional<List<Student>> {
//        return Optional.of(studentRepository.findByFacultyIdsAndStatus(faculty_id, studentStatus))
//    }

    fun parseFileToGetInfo(studentInfo: MultipartFile, faculty: Faculty): StudentDto {
        val fileContent = JSONObject(String(studentInfo.bytes))
        val fileContentResult: JSONArray = fileContent.getJSONArray("result")
        val userContent: JSONObject? = fileContentResult.getJSONObject(0).getJSONObject("user")
        val personalInformation: JSONObject? = userContent?.getJSONObject("personal_information")
        val generalInformation: JSONObject? = personalInformation?.getJSONObject("general_information")
        val firstName: String? = generalInformation?.getString("firstname")
        val lastName: String? = generalInformation?.getString("lastname")
        val email: String? = generalInformation?.getString("email")
        val fatherInitials: String? = generalInformation?.getString("father_initials")
        val citizenship: String? = generalInformation?.getString("citizenship")
        val phone: String? = generalInformation?.getString("phone")

        return StudentDto(email!!, firstName!!, lastName!!, fatherInitials!!, citizenship!!, phone!!, mutableListOf(faculty))
    }
}