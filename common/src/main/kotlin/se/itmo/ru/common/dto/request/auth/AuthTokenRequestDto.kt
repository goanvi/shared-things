package se.itmo.ru.common.dto.request.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class AuthTokenRequestDto(
        @NotNull
        @JsonProperty("username")
        var username: String,

        @NotNull
        @JsonProperty("password")
        var password: String
)