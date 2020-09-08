package com.md.service

import com.md.model.*
import com.md.model.dto.StudentStatusUpdateDto
import com.md.repository.FacultyRepository
import com.md.repository.StudFacRepository
import com.md.repository.StudentRepository
import com.md.service.blobStorage.AzureBlobStorageService
import com.md.service.excel.GenerateExcelService
import org.json.JSONArray
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class StudentService(
    private val studentRepository: StudentRepository,
    private val facultyRepository: FacultyRepository,
    private val studFacRepository: StudFacRepository,
    private val azureBlobStorageService: AzureBlobStorageService,
    private val downstreamService: DownstreamService,
    private val generateExcelService: GenerateExcelService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StudentService::class.java)
    }

    @Value("\${localPath}")
    private val localPath: String? = null

    fun addNewStudent(studentDocuments: List<MultipartFile>, faculty: Faculty): StudentWithStatus {
        LOGGER.info("addNewStudent")
        var studentDto: StudentDto? = null
        studentDocuments.map {
            studentDocument ->
            if(studentDocument.originalFilename.contains("studentInfo")) {
                studentDto = parseFileToGetInfo(studentDocument, faculty)
            }
        }

        azureBlobStorageService.saveStudentDocuments(faculty.container_name, studentDocuments, studentDto!!.director)
        val optionalStudent: Optional<Student> = studentRepository.findByEmail(studentDto!!.email)
        if(optionalStudent.isPresent) {
            val student: Student = optionalStudent.get()
            faculty.students!!.add(student)
            facultyRepository.save(faculty)

            val studFacOptional = studFacRepository.findByStudidAndFacid(student.id, faculty.id)
            if (!studFacOptional.isPresent) {
                val studFac = StudFac(student.id, faculty.id, StudentStatus.REGISTERED)
                studFacRepository.save(studFac)
            } else {
                val studFac = studFacOptional.get()
                studFac.status = StudentStatus.REGISTERED
                studFacRepository.save(studFac)
            }

            val studentWithStatus = StudentWithStatus(student.id, student.email, student.firstName, student.lastName,
                student.father_initials, student.citizenship, student.phone, student.director, StudentStatus.REGISTERED)
            return studentWithStatus
        }

        val student = studentRepository.save(studentDto?.toStudent())
        val studFacOptional = studFacRepository.findByStudidAndFacid(student!!.id, faculty.id)
        val studFac = studFacOptional.get()
        studFac.status = StudentStatus.REGISTERED
        studFacRepository.save(studFac)
        return StudentWithStatus(
                student!!.id, student.email, student.firstName, student.lastName,
                student.father_initials, student.citizenship, student.phone, student.director, StudentStatus.REGISTERED)
    }

    fun updateDocumentsForExistingStudent(studentDocuments: List<MultipartFile>, faculty: Faculty, student: Student): StudentWithStatus {
        LOGGER.info("updateDocumentsForExistingStudent")
        val optionalStudFac: Optional<StudFac> = studFacRepository.findByStudidAndFacid(student.id, faculty.id)

        if(optionalStudFac.isPresent) {
            val studFac = optionalStudFac.get()
            studFac.status = StudentStatus.DOCUMENTS_RESENT
            azureBlobStorageService.saveStudentDocuments(faculty.container_name, studentDocuments, student.director)
            return StudentWithStatus(student.id, student.email, student.firstName, student.lastName,
            student.father_initials, student.citizenship, student.phone, student.director, studFac.status)
        } else throw Exception("Intrarea nu a fost gasita in baza de date")
    }

    fun getStudents(): Optional<List<Student>> = Optional.of(studentRepository.findAll())

    fun getStudent(studentId: UUID): Optional<Student> = studentRepository.findById(studentId)

    fun updateStudentObject(faculty: Faculty, student: Student, status: String): StudentWithStatus {
        LOGGER.info("updateStudentObject")
        val optionalStudFac: Optional<StudFac> = studFacRepository.findByStudidAndFacid(student.id, faculty.id)

        if(optionalStudFac.isPresent) {
            val studFac = optionalStudFac.get()
            studFac.status = StudentStatus.valueOf(status)
            studFacRepository.save(studFac)

            val statusUpdate = StudentStatusUpdateDto(status, student.id, faculty.id)
            downstreamService.notifyStudentApp(statusUpdate)

            return StudentWithStatus(student.id, student.email, student.firstName, student.lastName,
                student.father_initials, student.citizenship, student.phone, student.director, studFac.status)
        } else throw Exception("Intrarea nu a fost gasita in baza de date")
    }

    fun getStudentDocuments(containerName: String, student: Student):  HashMap<String, String>  {
        LOGGER.info("getStudentDocuments")
        return azureBlobStorageService.getStudentDocuments(containerName, student.director)
    }

    fun getStudentsFiltered(faculty: Faculty, studentStatus: String): MutableList<StudentWithStatus> {
        if(studentStatus == "all") {
            return mapStudents(faculty, "all")
        }

        return mapStudents(faculty, studentStatus)
    }

    fun mapStudents(faculty: Faculty, studentStatus: String): MutableList<StudentWithStatus> {
        val studFacList: List<StudFac> = studFacRepository.findByFacid(faculty.id)
        val students = faculty.students
        val studentsWithStatus: MutableList<StudentWithStatus> = ArrayList()
        for (student in students!!) {
            val it: List<StudFac> = studFacList.filter { it -> it.studid == student.id }
            val status = it[0].status
            if(studentStatus === "all") {
                val studentWithStatus = StudentWithStatus(
                    student.id, student.email, student.firstName, student.lastName,
                    student.father_initials, student.citizenship, student.phone, student.director, it[0].status
                )
                studentsWithStatus.add(studentWithStatus)
            } else if(status === StudentStatus.valueOf(studentStatus)) {
                val studentWithStatus = StudentWithStatus(
                    student.id, student.email, student.firstName, student.lastName,
                    student.father_initials, student.citizenship, student.phone, student.director, it[0].status
                )
                studentsWithStatus.add(studentWithStatus)
            }
        }
        return studentsWithStatus
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

    fun generateExcel(faculty: Faculty): String {
        generateExcelService.writeToExcelFile(faculty)
        val link: String = azureBlobStorageService.uploadStudentsExcel(faculty.container_name)

        val dir = File(localPath)
        if (dir.isDirectory) {
           File(dir, "students.xlsx").delete()
        }

        return link
    }
}