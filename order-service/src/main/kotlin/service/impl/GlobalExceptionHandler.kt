package com.buoyancy.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.model.dto.rest.MessageDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice

class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}
    @ExceptionHandler( BadRequestException::class )
    fun processBadRequestException(exception: Exception): ResponseEntity<MessageDto> {

        log.warn { "Bad request exception thrown: ${ exception.message }" }
        val status = BAD_REQUEST.value()

        return when (exception.message) {

            null -> ResponseEntity.status(status)
                .body(MessageDto(status, "Bad request"))

            else -> ResponseEntity.status(status)
                .body(MessageDto(status, exception.message!!))
        }
    }

    @ExceptionHandler( MethodArgumentNotValidException::class )
    fun processValidationException(exception: MethodArgumentNotValidException): ResponseEntity<MessageDto> {

        log.warn { "Validation exception thrown: ${ exception.message }" }
        val status = INTERNAL_SERVER_ERROR.value()
        val errors = exception.bindingResult?.allErrors

        return ResponseEntity.status(status)
            .body(MessageDto(
                statusCode = status,
                message = if (!errors.isNullOrEmpty()) buildValidationErrorMessage(errors) else "Bad request"
            ))
    }

    private fun buildValidationErrorMessage(errors : List<ObjectError>): String {
        val str = StringBuilder()
        for (e in errors) {
            val fieldError = e as FieldError
            str.append(fieldError.field)
                .append('=')
                .append(fieldError.rejectedValue)
                .append(", ")
        }
        str.deleteCharAt(str.length - 1)
        str.deleteCharAt(str.length - 1)
        return str.toString()
    }
}