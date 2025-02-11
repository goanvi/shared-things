package se.itmo.ru.bookings.rest.controller.exceptionhandler

data class ErrorResponse(
    val message: String? = null,
    val statusCode: Int,
    val trace: String? = null
)
