package se.itmo.ru.common.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class ApiErrorDto(
    @NotNull
    @JsonProperty("code")
    val code: String,
    @NotNull
    @JsonProperty("message")
    val message: String?
)