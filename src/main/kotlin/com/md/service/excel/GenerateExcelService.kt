package com.md.service.excel

import com.md.model.Faculty
import com.md.service.blobStorage.AzureBlobStorageService
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import org.springframework.stereotype.Component

@Component
class GenerateExcelService(
    private val azureBlobStorageService: AzureBlobStorageService
) {

    private val filepath = "/Users/arascol/Documents/alice/disertatie/be/data/excel.xlsx"

    fun writeToExcelFile(faculty: Faculty) {
        //Instantiate Excel workbook:
        val xlWb = XSSFWorkbook()
        //Instantiate Excel worksheet:
        var xlWs = xlWb.createSheet()
        xlWs = createHeaderForExcelSheet(xlWs)

        var rowNumber = 1
        //Cell index specifies the column within the chosen row (starting at 0):
        for (student in faculty.students!!) {
            var columnNumber = 0
            xlWs.createRow(rowNumber).createCell(columnNumber++).setCellValue(student.id.toString())
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.email)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.firstName)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.lastName)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.father_initials)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.citizenship)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.phone)
            xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(student.status.toString())

            val studentDocuments: HashMap<String, String> = azureBlobStorageService.getStudentDocuments(faculty.container_name, student.director)
            for ((name, link) in studentDocuments) {
                xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue(link)
            }
            rowNumber++
        }

        //Write file:
        val outputStream = FileOutputStream(filepath)
        xlWb.write(outputStream)
    }

    fun createHeaderForExcelSheet(xlWs: XSSFSheet): XSSFSheet {
        val rowNumber = 0
        var columnNumber = 0
        xlWs.createRow(rowNumber).createCell(columnNumber++).setCellValue("id")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Email")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("First name")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Last name")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Father initials")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Citizenship")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Phone")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Status")

        // documents
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Student Info")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Adeverinta medicala")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Carte de identitate")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Cerere de inscriere")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Certificat de comp lingvistica")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Certificat de casatorie")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Certificat de nastere")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Declaratie pe propria raspundere - depunere documente")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Declaratie pe propria raspundere - loc buget")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Declaratie pe propria raspundere - veridicitate date")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Diploma de bacalaureat")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Dovada de plata a taxei")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Diploma de bacalaureat")
        xlWs.getRow(rowNumber).createCell(columnNumber++).setCellValue("Foaie matricola")
        xlWs.getRow(rowNumber).createCell(columnNumber).setCellValue("Fotografie")

        return xlWs
    }
}