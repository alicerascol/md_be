package com.md.service

import com.md.model.*
import com.md.model.dto.StudentStatusUpdateDto
import com.md.repository.StudentRepository
import com.md.service.blobStorage.AzureBlobStorageService
import com.md.service.excel.GenerateExcelService
import org.json.JSONArray
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*
import org.json.JSONObject;
import kotlin.collections.HashMap

@Component
class StudentService(
    private val studentRepository: StudentRepository,
    private val azureBlobStorageService: AzureBlobStorageService,
    private val downstreamService: DownstreamService,
    private val generateExcelService: GenerateExcelService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentService::class.java)
    }

    fun addNewStudent(studentDocuments: List<MultipartFile>, faculty: Faculty): Student? {
        LOGGER.info("addNewStudent")
        var studentDto: StudentDto? = null
        studentDocuments.map {
            studentDocument ->
            if(studentDocument.originalFilename.contains("studentInfo")) {
                studentDto = parseFileToGetInfo(studentDocument, faculty)
            }
        }

        azureBlobStorageService.saveStudentDocuments(faculty.container_name, studentDocuments, studentDto!!.director)
        return studentRepository.save(studentDto?.toStudent())
    }

    fun updateDocumentsForExistingStudent(studentDocuments: List<MultipartFile>, faculty: Faculty, student: Student): Student? {
        LOGGER.info("updateDocumentsForExistingStudent")
        student.status = StudentStatus.DOCUMENTS_RESENT
        azureBlobStorageService.saveStudentDocuments(faculty.container_name, studentDocuments, student.director)
        return studentRepository.save(student)
    }

    fun getStudents(): Optional<List<Student>> = Optional.of(studentRepository.findAll())

    fun getStudent(studentId: UUID): Optional<Student> = studentRepository.findById(studentId)

    fun updateStudentObject(student: Student, status: String): Optional<Student> {
        LOGGER.info("updateStudentObject")
        student.status = StudentStatus.valueOf(status)
        val optionalStudent: Optional<Student> =  Optional.of(studentRepository.save(student))
        try {
            if(optionalStudent.isPresent) {
                val newStudent = optionalStudent.get()
                val statusUpdate = StudentStatusUpdateDto(status, newStudent.id)
                downstreamService.notifyStudentApp(statusUpdate)
            }
        } catch (ex: Exception) {
            LOGGER.error(ex.message)
        }
        return optionalStudent
    }

    fun getStudentDocuments(containerName: String, student: Student):  HashMap<String, String>  {
        LOGGER.info("getStudentDocuments")
        return azureBlobStorageService.getStudentDocuments(containerName, student.director)
    }

    fun getStudentsFiltered(faculty: Faculty, studentStatus: String): List<Student>? {
        if(studentStatus == "all")
            return faculty.students

        return faculty.students?.filter {student -> student.status ==  StudentStatus.valueOf(studentStatus)}
    }

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

        var studentDirector = firstName?.toLowerCase() + lastName?.toLowerCase() + fatherInitials?.toLowerCase()

        return StudentDto(email!!, firstName!!, lastName!!, fatherInitials!!, citizenship!!, phone!!, studentDirector, mutableListOf(faculty))
    }

    fun generateDataForExcel(faculty: Faculty): MutableList<HashMap<String, String>> {
        val students = mutableListOf<HashMap<String, String>>()
        for (student in faculty.students!!) {
            var studentData: HashMap<String, String> = HashMap()
            studentData["id"] = student.id.toString()
            studentData["email"] = student.email
            studentData["firstName"] = student.firstName
            studentData["lastName"] = student.lastName
            studentData["father_initials"] = student.father_initials
            studentData["citizenship"] = student.citizenship
            studentData["phone"] = student.phone
            val studentDocuments: HashMap<String, String> = azureBlobStorageService.getStudentDocuments(faculty.container_name, student.director)
            for ((name, link) in studentDocuments) {
                if (name.contains("Certificatul de nastere")) {
                    studentData["certificat_nastere"] = link
                }
                if (name.contains("cerere de inscriere")) {
                    studentData["cerere_inscriere"] = link
                }
                if (name.contains("Declaratie pe propria raspundere - Depunere documente")) {
                    studentData["declaratie_depunere_documente"] = link
                }
                if (name.contains("Declaratie pe propria raspundere - Loc bugetat")) {
                    studentData["declaratie_loc_buget"] = link
                }
                if (name.contains("Certificatul de casatorie")) {
                    studentData["certificat_casatorie"] = link
                }
                if (name.contains("Dovada de plata a taxei de inscriere")) {
                    studentData["dovada_plata"] = link
                }
                if (name.contains("Carte de identitate")) {
                    studentData["carte_identitate"] = link
                }
                if (name.contains("bacalaureat")) {
                    studentData["diploma_bacalaureat"] = link
                }
                if (name.contains("Adeverinta medicala")) {
                    studentData["adeverinta_medicala"] = link
                }
                if (name.contains("competenta lingvistica")) {
                    studentData["competenta_lingvistica"] = link
                }
                if (name.contains("Veridicitate date")) {
                    studentData["declaratie_veridicitate"] = link
                }
                if (name.contains("fotografie")) {
                    studentData["fotografie"] = link
                }
                if (name.contains("Foaia matricola eliberata de liceu")) {
                    studentData["foaie_matricola_liceu"] = link
                }
                if (name.contains("studentInfo")) {
                    studentData["studentInfo"] = link
                }
            }
            students.add(studentData)
        }
        return students
    }

    fun generateExcel(faculty: Faculty) {
        generateExcelService.writeToExcelFile(faculty)
    }
}