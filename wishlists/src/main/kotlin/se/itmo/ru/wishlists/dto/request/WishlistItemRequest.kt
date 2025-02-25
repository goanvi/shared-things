package se.itmo.ru.wishlists.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class WishlistItemRequest(

    @field:NotNull(message = "owner id cannot be null")
    val owner: UUID,

    @field:NotBlank(message = "title cannot be blank")
    @field:Size(max = 300, message = "title cannot be more than 300 characters.")
    val title: String,

    val description: String? = null,

    )
