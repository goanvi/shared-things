package se.itmo.ru.wishlists.controller.exceptionhandler

import org.springframework.dao.NonTransientDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import se.itmo.ru.wishlists.exception.ServiceException

@RestControllerAdvice(annotations = [RestController::class])
class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        value = [
            MethodArgumentNotValidException::class,
            HttpMessageNotReadableException::class,
            WebExchangeBindException::class,
            UnsupportedMediaTypeStatusException::class
        ]
    )
    fun validationExceptionHandler(ex: Exception): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.BAD_REQUEST.value(),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [ServiceException::class])
    fun serviceExceptionHandler(ex: ServiceException): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.BAD_REQUEST.value(),
        )
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(value = [NonTransientDataAccessException::class])
    fun entityExceptionHandler(ex: NonTransientDataAccessException): ErrorResponse {
        return ErrorResponse(
            message = ex.message,
            statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value(),
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