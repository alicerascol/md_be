package com.md.service

import org.slf4j.LoggerFactory
import java.io.File
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object Utils {

    private val LOGGER = LoggerFactory.getLogger(Utils::class.java)

    fun hashString(input: String): String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(input.toByteArray())
        return DatatypeConverter.printHexBinary(bytes).toUpperCase()
    }

    fun readFileDirectlyAsText(configFileName: String, localPath: String): String {
        LOGGER.info("readFileDirectlyAsText")
        val dir = File(localPath)
        if (dir.isDirectory) {
            var file = File(configFileName)
            if(file.exists())
                File(dir, configFileName).delete()
        }
        return File(localPath + configFileName).readText(Charsets.UTF_8)
    }
}