package se.itmo.ru.bookings.controller.exceptionhandler

data class ErrorResponse(
    val message: String? = null,
    val statusCode: Int,
    val trace: String? = null
)
