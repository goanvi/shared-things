package se.itmo.ru.common.dto.response.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class AuthTokenResponseDto(
        @NotNull
        @JsonProperty("token")
        val token: String
)