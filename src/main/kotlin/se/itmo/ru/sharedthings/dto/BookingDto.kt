package se.itmo.ru.sharedthings.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.validator.annotation.FutureDate
import java.time.LocalDateTime

data class BookingDto(

    @field:NotNull(message = "booking id cannot be null")
    var bookingId: Int = 0,

    var renter: Account? = null,

    @field:NotNull(message = "start date cannot be null")
    var startDate: LocalDateTime = LocalDateTime.now(),

    @field:NotNull(message = "end date cannot be null")
    @field:Future(message = "end date cannot be in the past")
    val endDate: LocalDateTime,

    @field:NotNull(message = "booking status cannot be null")
    var status: BookingStatus = BookingStatus.OPEN,

    val description: String? = null,

    @field:NotNull(message = "items cannot be null")
    var bookedItems: Set<Item> = emptySet(),

    @field:NotNull(message = "items ids cannot be null")
    val bookedItemsIds: Set<Int> = emptySet(),
)
