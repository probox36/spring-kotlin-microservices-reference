package com.buoyancy.common.exceptions

import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.utils.get
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var messages : MessageSource

    @ExceptionHandler( BadRequestException::class )
    fun processBadRequestException(exception: BadRequestException): MessageDto {
        log.warn { "Bad request exception thrown: ${ exception.message }" }
        return MessageDto(
            BAD_REQUEST.value(),
            exception.message ?: messages.get("rest.exceptions.badrequest")
        )
    }

    @ExceptionHandler(ConflictException::class )
    fun processConflictException(exception: ConflictException): MessageDto {
        log.warn { "Conflict exception thrown: ${ exception.message }" }
        return MessageDto(
            CONFLICT.value(),
            exception.message ?: messages.get("rest.exceptions.conflict")
        )
    }

    @ExceptionHandler(InternalErrorException::class )
    fun processInternalErrorException(exception: InternalErrorException): MessageDto {
        log.warn { "Internal error exception thrown: ${ exception.message }" }
        return MessageDto(
            INTERNAL_SERVER_ERROR.value(),
            exception.message ?: messages.get("rest.exceptions.internalerror")
        )
    }

    @ExceptionHandler(NotFoundException::class )
    fun processNotFoundException(exception: NotFoundException): MessageDto {
        log.warn { "Not found exception thrown: ${ exception.message }" }
        return MessageDto(
            NOT_FOUND.value(),
            exception.message ?: messages.get("rest.exceptions.notfound")
        )
    }

    @ExceptionHandler(UnauthorizedException::class )
    fun processUnauthorizedException(exception: UnauthorizedException): MessageDto {
        log.warn { "Unauthorized exception thrown: ${ exception.message }" }
        return MessageDto(
            UNAUTHORIZED.value(),
            exception.message ?: messages.get("rest.exceptions.unauthorized")
        )
    }

    @ExceptionHandler( MethodArgumentNotValidException::class )
    fun processValidationException(exception: MethodArgumentNotValidException): ResponseEntity<MessageDto> {

        log.warn { "Validation exception thrown: ${ exception.message }" }
        val status = INTERNAL_SERVER_ERROR.value()
        val errors = exception.bindingResult.allErrors

        return ResponseEntity.status(status)
            .body(MessageDto(
                statusCode = status,
                message = if (errors.isNotEmpty()) buildValidationErrorMessage(errors) else "Bad request"
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