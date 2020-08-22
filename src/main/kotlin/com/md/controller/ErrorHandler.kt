package com.md.controller

import com.md.dto.ErrorDto
import com.md.dto.FieldErrorDto
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ErrorHandler {

  @ExceptionHandler(MethodArgumentNotValidException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  fun handleException(ex: MethodArgumentNotValidException): ErrorDto {
    val result = ex.bindingResult
    val fieldErrors = result.fieldErrors
      .map { FieldErrorDto(it.field, it.defaultMessage) }
      .sortedWith(Comparator.comparing(FieldErrorDto::field).thenBy(FieldErrorDto::message))

    return ErrorDto("Invalid request", fieldErrors)
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  fun handleMissingRequestBody(ex: HttpMessageNotReadableException): ErrorDto {
    return ErrorDto("Malformed JSON request")
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  fun handleArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ErrorDto {
    return ErrorDto("Invalid request, path parameter [${ex.name}] is not valid")
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  fun handleUnsupportedMethod(response: HttpServletResponse) {
    response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value())
  }

  @ExceptionHandler(Exception::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  fun handleException(): ErrorDto {
    return ErrorDto("Internal error, please try again later")
  }
}
