package se.itmo.ru.wishlists.rest.controller.exceptionhandler

data class ErrorResponse(
    val message: String? = null,
    val statusCode: Int,
    val trace: String? = null
)
