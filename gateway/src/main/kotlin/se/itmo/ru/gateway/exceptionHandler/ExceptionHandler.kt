package se.itmo.ru.gateway.exceptionHandler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import se.itmo.ru.common.dto.response.ApiErrorDto

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ResponseStatusException::class)
    fun handleException(ex: ResponseStatusException): ResponseEntity<ApiErrorDto> {
        return ResponseEntity.status(ex.statusCode).body(
                ApiErrorDto(
                        ex.statusCode.toString(),
                        ex.reason.toString(),
                )
        )
    }

}