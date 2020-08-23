package com.md.service

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object Utils {

    fun hashString(input: String): String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(input.toByteArray())
        return DatatypeConverter.printHexBinary(bytes).toUpperCase()
    }

}