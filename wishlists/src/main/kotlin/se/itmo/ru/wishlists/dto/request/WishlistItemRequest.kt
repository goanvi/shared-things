package se.itmo.ru.wishlists.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class WishlistItemRequest(

    @field:NotNull(message = "owner id cannot be null")
    @field:Min(1, message = "owner can not be less than 1")
    val owner: UUID,

    @field:NotBlank(message = "title cannot be blank")
    @field:Size(max = 300, message = "title cannot be more than 300 characters.")
    val title: String,

    val description: String? = null,

)
