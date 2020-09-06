package com.md.service

import com.md.model.dto.StudentStatusUpdateDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DownstreamService(
    private val restTemplate: RestTemplate
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DownstreamService::class.java)
    }

    @Value("\${studentApp.url}")
    private val URL: String? = null

    fun notifyStudentApp(studentStatusUpdate: StudentStatusUpdateDto) {
        LOGGER.debug("Call notifyStudentApp: {} for: {}", URL, studentStatusUpdate)
        return try {
            val requestEntity: HttpEntity<StudentStatusUpdateDto> = HttpEntity(studentStatusUpdate)
            val responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, Void::class.java)
        } catch (ex: Exception) {
            LOGGER.error("[{}] Http Client Error: Unable to {}: {}", ex)
            throw ex
        }
    }
}