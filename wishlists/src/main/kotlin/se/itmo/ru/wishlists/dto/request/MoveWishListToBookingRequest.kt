package se.itmo.ru.wishlists.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class MoveWishListToBookingRequest(
    @field:NotNull(message = "wishlistId cannot be null")
    @field:JsonProperty("wishlist_id")
    val wishlistId: UUID,

    @field:NotNull(message = "foundItemId cannot be null")
    @field:JsonProperty("found_item_id")
    val foundItemId: UUID,

    @field:NotNull(message = "endDateOfBooking cannot be null")
    @field:Future(message = "endDateOfBooking must be in the future")
    @field:JsonProperty("end_date_of_booking")
    val endDateOfBooking: LocalDateTime
)