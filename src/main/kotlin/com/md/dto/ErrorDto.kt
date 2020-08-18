package com.md.dto

data class ErrorDto(val message: String, val errors: List<FieldErrorDto>) {
    constructor(message: String): this(message, emptyList())
}

data class FieldErrorDto(val field: String, val message: String?)
