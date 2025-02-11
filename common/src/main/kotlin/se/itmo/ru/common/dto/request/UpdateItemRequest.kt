package se.itmo.ru.common.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateItemRequest(

    @field:NotBlank(message = "name cannot be blank")
    @field:Size(max = 100, message = "name cannot be longer than 100 characters")
    val name: String,

    @field:Size(min = 1, max = 1000, message = "description cannot be longer than 1000 characters")
    val description: String? = null,
)
