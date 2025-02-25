package se.itmo.ru.authservice.controllers.exceptionHandler

data class ErrorResponse(
    val message: String? = null,
    val statusCode: Int,
    val trace: String? = null
)
