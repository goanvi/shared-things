package se.itmo.ru.common.dto.request.users

import org.jetbrains.annotations.NotNull

data class CreateUserRequestDto(
        @field:NotNull
        var username: String,
        @field:NotNull
        var password: String,
        @field:NotNull
        var role: String

)