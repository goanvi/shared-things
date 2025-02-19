package se.itmo.ru.common.dto.response.users

import org.jetbrains.annotations.NotNull
import java.util.*

data class CreateUserResponseDto (
        var id: UUID,

        var username: String,

        var role: String,
)