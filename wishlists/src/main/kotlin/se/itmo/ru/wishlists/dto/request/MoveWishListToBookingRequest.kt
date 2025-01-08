package se.itmo.ru.wishlists.dto.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.*

data class MoveWishListToBookingRequest(
    @field:NotNull(message = "wishlistId cannot be null")
    @field:Min(1, message = "wishlistId cannot be less than 1")
    val wishlistId: UUID,

    @field:NotNull(message = "foundItemId cannot be null")
    @field:Min(1, message = "foundItemId cannot be less than 1")
    val foundItemId: UUID,

    @field:NotNull(message = "endDateOfBooking cannot be null")
    @field:Future
    val endDateOfBooking: String
)