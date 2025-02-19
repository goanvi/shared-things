package se.itmo.ru.common.dto.request.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class ValidateTokenRequestDto (
        @NotNull
        @JsonProperty
        val token: String
)