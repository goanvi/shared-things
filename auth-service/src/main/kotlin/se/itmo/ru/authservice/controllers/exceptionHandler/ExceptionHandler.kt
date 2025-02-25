package se.itmo.ru.authservice.controllers.exceptionHandler

import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(annotations = [RestController::class])
class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [MethodArgumentNotValidException::class, HttpMessageNotReadableException::class])
    fun validationExceptionHandler(ex: Exception): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.BAD_REQUEST.value(),
        )
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = [AuthorizationDeniedException::class])
    fun accessDenied(ex: AuthorizationDeniedException): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.FORBIDDEN.value(),
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [Exception::class])
    fun unhandledError(ex: Exception): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            trace = ex.stackTraceToString()
        )
    }
}