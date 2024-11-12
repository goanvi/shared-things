package se.itmo.ru.sharedthings.controller.exceptionhandler

data class ErrorResponse(
    val message: String? = null,
    val statusCode: Int,
    val trace: String? = null
)
